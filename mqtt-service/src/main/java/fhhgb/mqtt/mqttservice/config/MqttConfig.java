package fhhgb.mqtt.mqttservice.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.UUID;

@Component
public class MqttConfig {

    @Value("${mqtt.host}")
    private String host;

    /**
     * static publisher and subscriber - connect only once to MQTT Server
     */
    private static IMqttClient publisher = null;
    private static IMqttClient subscriber = null;

    private IMqttClient getPublisher() {
        if (publisher == null) {
            publisher = createMQttClient();
        }
        return publisher;
    }

    private IMqttClient getSubscriber() {
        if (subscriber == null) {
            subscriber = createMQttClient();
        }
        return subscriber;
    }

    /**
     * creates MQTT client and sets connection options
     * handles MQTT connection
     */
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

    /**
     *  publishes message on given topic
     */
    public void publishMessage(String topic, String message) {
        try {
            IMqttClient publisher = getPublisher();
            if (publisher != null && publisher.isConnected()) {
                publisher.publish(topic, buildMessage(message));
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * subscribes to topic and adds messageListener to handele messages
     */
    public void subscribe(String topic, IMqttMessageListener messageListener) {
        try {
            IMqttClient subscriber = getSubscriber();
            if (subscriber != null && subscriber.isConnected()) {
                subscriber.subscribe(topic, messageListener);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * generates Mqtt Message object from given string
     */
    private MqttMessage buildMessage(String message) {
        byte[] payload = message.getBytes();
        MqttMessage msg = new MqttMessage(payload);
        msg.setQos(0);
        msg.setRetained(true);
        return msg;
    }

    /**
     * Closes MQTT connection when component is destroyed
     */
    @PreDestroy
    private void destroy() {
        try {
            getPublisher().disconnect();
            getSubscriber().disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
