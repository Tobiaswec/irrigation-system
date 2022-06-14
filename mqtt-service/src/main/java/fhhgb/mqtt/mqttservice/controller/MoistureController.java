package fhhgb.mqtt.mqttservice.controller;

import fhhgb.mqtt.mqttservice.MeasurementEntity;
import fhhgb.mqtt.mqttservice.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MoistureController {

    @Autowired
    private MeasurementRepository repository;

    /**
     * Calculates moisture of last measurements
     * returns moisture in percentage
     */
    @GetMapping("/moisture/getPercentage")
    public int getMoisturePercentage() {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "timestamp");
        Optional<MeasurementEntity> entity = repository.findAll(pageable).getContent().stream().findFirst();
        return entity.map(it -> (int) (it.getPercentage() * 100)).orElse(0);
    }

    /**
     * Calculates moisture avg over last 1000 measurements
     * returns avg in percentage
     */
    @GetMapping("/moisture/getAvgPercentage")
    public int getMoistureAvgPercentage() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.Direction.DESC, "timestamp");
        List<MeasurementEntity> entity = repository.findAll(pageable).getContent();
        return (int) (entity.stream().map(MeasurementEntity::getPercentage).mapToDouble(Double::doubleValue).summaryStatistics().getAverage() * 100);
    }
}
