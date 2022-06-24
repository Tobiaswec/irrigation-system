
<center><h1>Bewässerungssystem</h1></center>
<center><h3>Julian Pichler,  Tobias Wecht</h3></center>


## Zielsetzung
Ziel war es eine über einen Alexa-Skill steuerbare, Bewässerungsanlage zu bauen und durch die Automatisierung, der Bewässerung, Wasser zu sparen.
Die Wasserknappheit wird weltweit immer schlimmer und wir wollten mit diesem Projekt auf die bewusste Nutzung von dieser wichtige Resource aufmerksam zu machen.

Das System soll es ermöglichen eine Pflanze über Sprach-Input in verschiedenen Modi zu bewässern, aber auch die Feuchtigkeit der Erde bzw. 
verschiedene statistische Kennzahlen darüber abzufragen. 
## Architektur
![alt text](/images/architecture.png)

Der Amazon Alexa Skill dient als Schnittstelle zum User. Der Skill kann Informationen zur momentanen oder durchschnittlichen Feuchtigkeit einer Pflanze abrufen und
bietet zwei Modi zum Gießen.

Das Spring Webservice verarbeitet die Requests des Alexa Skills und stellt die Daten der MongoDb bereit.
Darüber hinaus speichert es die Messergebnisse des ESP32 in die Datenbank und schickt diesem, über den MQTT Broker,
Instruktionen.

Die Bewässerung und Messungen erfolgen über einen ESP32, 
welcher mit einem Feuchtigkeitssensor und einer Wasserpumpe verbunden ist.
Das IoT Device kommuniziert die Messungen und 
erhält Instruktionen zum Starten der Wasserpumpe über einen MQTT Broker.

## Umsetzung

### Schaltung
Es wird eine 5V Wasserpumpe über ein 5V Relais mit dem 5V Pin(VIN) des ESP32 mit Spannung versorgt. Das Relais an sich wird einerseits über den 3.3V Pin des ESP32 mit Strom versorgt und wird über einen GPIO Pin geschalten. Dies geschieht im Code über ```digitalWrite(WATER_PORT, HIGH);```

Der Bodenfeuchtigkeitssensor benötigt ebenfalls eine Versorgungsspannung von 5V welche über den 5V Pin(VIN) des ESP32 geliefert wird. Mit dem Befehl analogRead(PIN_NR) kann der aktuelle Wert des Sensors ausgelesen werden. Geliefert wird ein Wert zwischen 4095(komplett trocken) und 2200(Sensor in Wasser).

Schaltungsaufbau:

![alt text](/images/schaltung.jpg)


### ESP32
Auf diesen Microcontroller läuft ein Programm, welches sich zuerst mit dem WLAN verbindet und anschließend versucht sich mit dem MQTT-Broker zu verbinden. Ist dies erfolgreich geschehen, beginnt der ESP32 in einem Intervall von 3 Sekunden die aktuelle Feuchtigkeit der Erde auszulesen und über eine MQTT-Message an das topic ```moisture``` zu senden. Weiters subscribed der ESP32 sich auf das Topic ```water``` und reagiert auf einkommende Nachrichten mit einer Callback-Methode. In den Nachrichten in diesem Topic befindet sich im Body jeweils die Dauer mit der die Wasserpumpe aktiviert werden soll. In der Callback-Methode wird diese Dauer ausgelesen und das Relais anschließend genau für diese Dauer eingeschaltet.

### Mosquitto MQTT Broker
Zuerst muss eine Mosquitto Server Konfiguration erstellt werden, in der Einstellungen für Port und
Host sowie zur Authentifizierung und Persistierung getroffen werden.

````editorconfig
persistence true
persistence_location /mosquitto/data/
log_dest file /mosquitto/log/mosquitto.log

listener 1883 0.0.0.0
## Authentication ##
allow_anonymous true
````

Anschließend kann Mosquitto MQTT Broker einfach als docker container gestartet werden.
````dockerfile
  mosquitto:
    image: eclipse-mosquitto
    container_name: mosquitto
    volumes:
      - './mosquitto/conf/mosquitto.conf:/mosquitto/config/mosquitto.conf'
      - './mosquitto/data/:/mosquitto/data/'
      - './mosquitto/log/:/mosquitto/log/'
    ports:
      - 1883:1883
      - 9001:9001
    networks:
      - mqtt_net
    user: 1883:1883
    environment:
      - PUID=1883
      - PGID=1883
````
### Spring Web Service

Das Service wurde in Java implementiert und für die MQTT Kommunikation wurde die Bibliothek ````org.eclipse.paho:org.eclipse.paho.client.mqttv3```` verwendet.
Der MQTT Client zum subskribieren und senden von Nachrichten wurde über folgenden Code konfiguriert. 

````java
private IMqttClient createMQttClient(){
        try {
            String id = UUID.randomUUID().toString();
            MqttClient client = new MqttClient("tcp://" + host + ":1883", id);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(120);
            client.connect(options);
            return client;
        } catch (MqttException e) {
            System.out.println("Connection to Broker failed!");
            e.printStackTrace();
        }
        return null;
    }
````
Das Spring Web Service kommuniziert über den MQTT Broker mit dem ESP32 und subskribiert dabei auf das Topic ```moisture``` um Messungen zu empfangen und published Instruktionen für den ESP32 auf das Topic ```water```.

Die empfangenen Messungen werden in der MongoDB Datenbank persistiert.
![alt text](/images/dbentries.png)

Um mit dem Alexa Skill zu kommunizieren werden Rest Calls benutzt. Im Server werden daher zwei Controller implementiert, welche die Messergebnisse in unterschiedlichen Representation zur Verfügung stellen.
Zusätzlich kann das Senden einer Bewässerungsinstruktion an den ESP32 über einen Endpoint getriggert werden.
### Alexa Skill
Für den Alexa-Skill wurden 4 Intents angelegt:

#### WaterIntent
Bei Aufruf dieses Intent wird die Pumpe für drei Sekunden aktiviert.

##### utterances:
![alt text](/images/waterIntent.png)

##### handler:
```js
const WaterIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'WaterIntent';
    },
    async handle(handlerInput) {
        var speakOutput = 'Ok, ich gieße!';
        let response = await logic.waterPlant(3000);

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
```


#### WaterStrongIntent
Bei Aufruf dieses Intent wird die Pumpe für sechs Sekunden aktiviert.


##### utterances:
![alt text](/images/waterIntentStrong.png)

##### handler:
```js
const WaterStrongIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'WaterStrongIntent';
    },
    async handle(handlerInput) {
        var speakOutput = 'Ok, ich gieße ausgiebig';
        let response = await logic.waterPlant(6000);
         
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
```


#### CurrentMoistureIntent
Dieser Aufruf gibt die zuletzt gemessene Feuchtigkeit in Prozent(%) aus.


##### utterances:
![alt text](/images/currentMoistureIntent.png)

##### handler:
```js
const CurrentMoistureIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'CurrentMoistureIntent';
    },
    async handle(handlerInput) {
        var speakOutput = 'die feuchtigkeit beträgt ';
        let response = await logic.getCurrent();
        speakOutput += response+"%";

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
```


#### AvgMoistureIntent
Dieser Aufruf gibt die durchschnittliche Feuchtigkeit der letzten 1000 Messungen in Prozent(%) aus.


##### utterances:
![alt text](/images/avgMoistureIntent.png)

##### handler:
```js
const AvgMoistureIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AvgMoistureIntent';
    },
    async handle(handlerInput) {
        var speakOutput = 'die durchschnittliche feuchtigkeit beträgt ';
        let response = await logic.getAvg();
        speakOutput += response+"%";

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
```



## Ergebnis

### Aufbau
![alt text](/images/schaltungReal.jpg)

### Feuchtigkeit ausgeben
![alt text](/images/moisture.jpg)

### avg Feuchtigkeit ausgeben
![alt text](/images/moistureAvg.jpg)

### Pflanze bewässern
![alt text](/images/water.jpg)

### Pflanze starke bewässern
![alt text](/images/waterStrong.jpg)


## Conclusion

### Spring Backend
Implementierung des MQTT-Brokers mit der verwendeten Bibliothek gestaltete sich  einfach, aber war sehr code lastig.

### Hardware
War der Aufwendigste Teil dieses Projekts, da das Elektronik-Know-How nur eingeschränkt vorhanden war. Dieses hat sich aber während des Projekts verbessert.

### Alexa Skill
Die Developer-Console macht das Gestalten eines Skills sehr einfach. Die meisten nötigen EInstellungen sind über das UI möglich und es sind nur wenige Zeilen Code zu produzieren und die Dokumentation ist sehr gut.

## Installationsanleitung
Das start.ps1 Skript oder manuel docker-compose ausführen.
Docker compose startet den Mosquitto MQTT Broker, das Backend Webservice und die Mongo Datenbank.

Start Skript:
<br>
.\start.ps1

Spring Webservice: http://localhost:8080 <br>
Mosquitto MQTT Broker: [tcp://localhost:1883](tcp://localhost:1883)<br>
MongoDB: [mongodb://localhost:27017](mongodb://localhost:27017) <br>
