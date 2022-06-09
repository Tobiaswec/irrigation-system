package fhhgb.mqtt.mqttservice.controller;

import fhhgb.mqtt.mqttservice.MeasurementEntity;
import fhhgb.mqtt.mqttservice.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/moisture")
public class MoistureController {
    @Autowired
    private MeasurementRepository repository;

    @GetMapping("/getPercentage")
    public double getMoisturePercentage(){
        Pageable pageable = PageRequest.of(0,1,Sort.Direction.DESC,"timestamp");
        Optional<MeasurementEntity> entity =  repository.findAll(pageable).getContent().stream().findFirst();
        return entity.map(MeasurementEntity::getPercentage).orElse(0.0);
    }
}
