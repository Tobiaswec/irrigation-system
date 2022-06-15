# irrigation-system
Irrigation System realized with Arduino, MQTT, Spring

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
Darüber hinaus speichert es die Messergebnisse des Arduinos in die Datenbank und schickt diesem, über den MQTT Broker,
Instruktionen.

Die Bewässerung und Messungen erfolgen über einen Arduino, 
welcher mit einem Feuchtigkeitssensor und einer Wasserpumpe verbunden ist.
Das IoT Device kommuniziert die Messungen und 
erhält Instruktionen zum Starten der Wasserpumpe über einen MQTT Broker.

## Umsetzung

### Schaltung
Es wird eine 5V Wasserpume über ein 5V Relais mit dem 5V Pin(VIN) des ESP32 mit Spannung versorgt. Das Relais an sich wird einerseits über den 3.3V Pin des ESP32 mit Strom versorgt und wird über einen GPIO Pin geschalten. Dies geschieht im Code über ```digitalWrite(WATER_PORT, HIGH);```

Der Bodenfeuchtigkeitssensor benötigt ebenfalls eine Versorgungsspannung von 5V welche über den 5V Pin(VIN) des ESP32 geliefert wird. Mit dem Befehl analogRead(PIN_NR) kann der aktuelle Wert des Sensors ausgelesen werden. Geliefert wird ein Wert zwischen 4095(komplett trocken) und 2200(Sensor in Wasser).

### ESP32
Auf diesen Microcontroller läuft ein Programm, welches sich zuerst mit dem WLAN verbindet und anschließend versucht sich mit dem MQTT-Broker zu verbinden. Ist dies erfolgreich geschehen, beginnt der ESP32 in einem Intervall von 3 Sekunden die aktuelle Feuchtigkeit der Erde auszulesen und über eine MQTT-Message an das topic ```/moisture``` zu senden. Weiters subscribed der ESP32 sich auf das Topic ```/water``` und reagiert auf einkommende Nachrichten mit einer Callback-Methode. In den Nachrichten in diesem Topic befindet sich im Body jeweils die Dauer mit der die Wasserpumpe aktiviert werden soll. In der Callback-Methode wird diese Dauer ausgelesen und das Relais anschließend genau für diese Dauer eingeschaltet.

### Alexa Skill
Für den Alexa-Skill wurden 4 Intents Angelegt:

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
![alt text](/images/waterStrongIntent.png)

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
Dieser Aufruf gibt die durschnittliche Feuchtigkeit der letzten 1000 Messungen in Prozent(%) aus.


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

### Feuchtigkeit ausgeben
### avg Feuchtigkeit ausgeben
### Pflanze bewässern
### Pflanze starke bewässern

Die Messungen werden in der Datenbank persistiert.
![alt text](/images/dbentries.png)

## Conclusion

## Installationsanleitung
Das start.ps1 Skript oder manuel docker-compose ausführen.
Docker compose startet den Mosquitto MQTT Broker, das Backend Webservice und die Mongo Datenbank.

Start Skript:
<br>
.\start.ps1

Spring Webservice: http://localhost:8080 <br>
Mosquitto MQTT Broker: [tcp://localhost:1883](tcp://localhost:1883)<br>
MongoDB: [mongodb://localhost:27017](mongodb://localhost:27017) <br>
