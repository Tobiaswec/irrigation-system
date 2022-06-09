package fhhgb.mqtt.mqttservice.repository;

import fhhgb.mqtt.mqttservice.MeasurementEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeasurementRepository extends MongoRepository<MeasurementEntity, ObjectId> {

}
