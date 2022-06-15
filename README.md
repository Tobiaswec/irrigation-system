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