package fhhgb.mqtt.mqttservice.controller;

import fhhgb.mqtt.mqttservice.MeasurementEntity;
import fhhgb.mqtt.mqttservice.config.MoistureSensorConfig;
import fhhgb.mqtt.mqttservice.config.MqttConfig;
import fhhgb.mqtt.mqttservice.repository.MeasurementRepository;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class MqttController {
    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MeasurementRepository repository;

    @Value("${mqtt.subTopic}")
    private String subTopic;

    @Value("${mqtt.pubTopic}")
    private String pubTopic;

    /**
     * Subscribes on Topic during Startup
     * Saves incoming measurements in DB
     */
    @PostConstruct
    public void processSubscriptions(){
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

    /**
     * publishes Message
     */
    @GetMapping("/waterPlant")
    public void waterPlant() {
        //message not relevant for subscriber - just activates water pump
        mqttConfig.publishMessage(pubTopic, "let it flow");
    }

}
