package fhhgb.mqtt.mqttservice.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.UUID;

@Component
public class MqttConfig {


    @Value("${mqtt.host}")
    private String host;

    @Bean
    private IMqttClient mqttPublisher() {
        try {
            String publisherId = UUID.randomUUID().toString();
            MqttClient client = new MqttClient("tcp://" + host + ":1883", publisherId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);
            return client;
        } catch (MqttException e) {
            System.out.println("Connection to Broker failed!");
            e.printStackTrace();
        }
        return null;
    }


    @Bean
    private IMqttClient mqttSubscriber() {
        try {
            String subscriberId = UUID.randomUUID().toString();
            MqttClient client = new MqttClient("tcp://" + host + ":1883", subscriberId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);
            return client;
        } catch (MqttException e) {
            System.out.println("Connection to Broker failed!");
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String topic, String message) {
        try {
            if (mqttPublisher() != null && !mqttPublisher().isConnected()) {
                return;
            }
            mqttPublisher().publish(topic, buildMessage(message));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, IMqttMessageListener messageListener) {
        try {
            if (mqttSubscriber() != null && !mqttSubscriber().isConnected()) {
                return;
            }
            mqttSubscriber().subscribe(topic, messageListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttMessage buildMessage(String message) {
        byte[] payload = message
                .getBytes();

        MqttMessage msg = new MqttMessage(payload);
        msg.setQos(0);
        msg.setRetained(true);
        return msg;
    }

    @PreDestroy
    private void destroy() {
        try {
            mqttPublisher().disconnect();
            mqttSubscriber().disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
