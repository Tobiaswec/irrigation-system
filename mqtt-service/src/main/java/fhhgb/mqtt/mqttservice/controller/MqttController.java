package fhhgb.mqtt.mqttservice.controller;

import fhhgb.mqtt.mqttservice.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class MqttController {
    @Autowired
    private MqttConfig mqttConfig;

    @PostConstruct
    public void processSubscriptions() throws InterruptedException {
        String subTopic="testSub";
        IMqttMessageListener listener = new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                byte[] payload = message.getPayload();
                System.out.println(new String(payload));
            }
        };
        mqttConfig.subscribe(subTopic,listener);
    }

    @PostConstruct
    public void sendMessage(){
        String pubTopic="testPub";
        String message = "testPub";
        mqttConfig.sendMessage(pubTopic,message);
    }

}
