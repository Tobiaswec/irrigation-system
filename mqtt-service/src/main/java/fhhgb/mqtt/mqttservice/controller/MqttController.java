package fhhgb.mqtt.mqttservice.controller;

import fhhgb.mqtt.mqttservice.MeasurementEntity;
import fhhgb.mqtt.mqttservice.config.MoistureSensorConfig;
import fhhgb.mqtt.mqttservice.config.MqttConfig;
import fhhgb.mqtt.mqttservice.repository.MeasurementRepository;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;

@RestController("/mqtt")
public class MqttController {
    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MeasurementRepository repository;

    @PostConstruct
    public void processSubscriptions() throws InterruptedException {
        String subTopic = "moisture";
        IMqttMessageListener listener = new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());

                MeasurementEntity entity = new MeasurementEntity();
                try {
                    entity.setMoisture(Double.parseDouble(payload));
                    entity.setPercentage(MoistureSensorConfig.getMoisturePercentage(entity.getMoisture()));
                    repository.save(entity);

                    System.out.println(payload);
                }catch (NumberFormatException ex){
                    System.out.println("Input not a number");
                }
            }
        };
        mqttConfig.subscribe(subTopic, listener);
    }

    @PostMapping("/mqtt/sendMessage")
    public void sendMessage() {
        String pubTopic = "water";
        String message = "let it flow";
        mqttConfig.sendMessage(pubTopic, message);
    }

}
