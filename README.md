# irrigation-system
Irrigation System realized with Arduino, MQTT, Spring

## Zielsetzung
Ziel war es eine über einen Alexa-Skill steuerbare, Bewässerungsanlage zu bauen und durch die Automatisierung, der Bewässerung, Wasser zu sparen.
Die Wasserknappheit wird weltweit immer schlimmer und wir wollten mit diesem Projekt auf die bewusste Nutzung von dieser wichtige Resource aufmerksam zu machen.

Das System soll es ermöglichen eine Pflanze über Sprach-Input in verschiedenen Modi zu bewässern, aber auch die Feuchtigkeit der Erde bzw. 
verschiedene statistische Kennzahlen darüber abzufragen. 
## Architektur
![alt text](/images/architecture.png)

## Umsetzung

### Schaltung
Es wird eine 5V Wasserpume über ein 5V Relais mit dem 5V Pin(VIN) des ESP32 mit Spannung versorgt. Das Relais an sich wird einerseits über den 3.3V Pin des ESP32 mit Strom versorgt und wird über einen GPIO Pin geschalten. Dies geschieht im Code über ```digitalWrite(WATER_PORT, HIGH);```

Der Bodenfeuchtigkeitssensor benötigt ebenfalls eine Versorgungsspannung von 5V welche über den 5V Pin(VIN) des ESP32 geliefert wird. Mit dem Befehl analogRead(PIN_NR) kann der aktuelle Wert des Sensors ausgelesen werden. Geliefert wird ein Wert zwischen 4095(komplett trocken) und 2200(Sensor in Wasser).

### ESP32
Auf diesen Microcontroller läuft ein Programm, welches sich zuerst mit dem WLAN verbindet und anschließend versucht sich mit dem MQTT-Broker zu verbinden. Ist dies erfolgreich geschehen, beginnt der ESP32 in einem Intervall von 3 Sekunden die aktuelle Feuchtigkeit der Erde auszulesen und über eine MQTT-Message an das topic ```/moisture``` zu senden. Weiters subscribed der ESP32 sich auf das Topic ```/water``` und reagiert auf einkommende Nachrichten mit einer Callback-Methode. In den Nachrichten in diesem Topic befindet sich im Body jeweils die Dauer mit der die Wasserpumpe aktiviert werden soll. In der Callback-Methode wird diese Dauer ausgelesen und das Relais anschließend genau für diese Dauer eingeschaltet.


## Ergebnis

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
