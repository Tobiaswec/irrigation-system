#include <SPI.h>
#include <PubSubClient.h>
#include <WiFi.h>

char WIFI_SSID[] = "Honeypot";
char WIFI_PASS[] = "servas123";
int status = WL_IDLE_STATUS;


//char mqttServerAddress[] = "test.mosquitto.org";
//char pubTopic[]= "S2110454011/moisture";
//char subTopic[]= "S2110454011/water";

char mqttServerAddress[] = "90.146.147.20";
int mqttServerPort = 1883;
int msgCounter = 0;
char pubTopic[]= "moisture";
char subTopic[]= "water";
WiFiClient client;
PubSubClient mqttClient(client);

int WATER_PORT = 4;
int HUMIDITY_PORT = 34;

void setup() {
   Serial.begin(9600);


  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Connecting to WiFi ..");
  while (WiFi.status() != WL_CONNECTED) {

    Serial.println(".");
    delay(1000);
  }
  Serial.println("[WIFI] Connected to the network ");
  Serial.println(WIFI_SSID);

  pinMode(WATER_PORT, OUTPUT);
  //set default to closed == LOW
  digitalWrite(WATER_PORT, HIGH);

  Serial.print("\n***Starting connection to MQTT broker: ");
  Serial.println(mqttServerAddress);

  mqttClient.setServer(mqttServerAddress,mqttServerPort);
  mqttClient.setCallback(callback);

  Serial.println("UP AND RUNNING");
}

void loop() {

      if(!mqttClient.connected()){
        reconnect();
      }
      mqttClient.loop();
      //readout Humidity Sensor
      int val = analogRead(HUMIDITY_PORT);
      Serial.println("val = ");
      Serial.println(val);

      //publish mqtt message
      char msg[16];
      itoa(val,msg,10);
      mqttClient.publish(pubTopic, msg);
      delay(3000);
}

void reconnect() {
  while (!mqttClient.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (mqttClient.connect("S2110454011/WaterIot")) {
      Serial.println("connected");
      mqttClient.publish(pubTopic, "hello world again", true);
      Serial.println("outTopic published");
      mqttClient.subscribe(subTopic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      delay(5000);
    }
  }
}


void callback(char* topic, byte* payload, unsigned int length) {
  //log message
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");


  char* s="";
  for (int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
    char x= (char)payload[i];

    s=appendCharToCharArray(s,x);
  }

   Serial.println();
   Serial.println(s);
  //get message value
  int duration=atoi(s);
  Serial.println(duration);

  Serial.println();
  Serial.println("FLOW");
  digitalWrite(WATER_PORT, LOW);
  delay(duration);
  Serial.println("NOT FLOW");
  digitalWrite(WATER_PORT, HIGH);
}

char* appendCharToCharArray(char* array, char a)
{
    size_t len = strlen(array);

    char* ret = new char[len+2];

    strcpy(ret, array);
    ret[len] = a;
    ret[len+1] = '\0';

    return ret;
}