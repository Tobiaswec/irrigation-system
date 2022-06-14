package fhhgb.mqtt.mqttservice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Document("measurements")
public class MeasurementEntity {

    @Id
    private ObjectId id;

    @Field
    private double moisture;

    @Field
    private double percentage;

    @Field
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public MeasurementEntity() {
    }

    public MeasurementEntity(ObjectId id, double moisture, double percentage, OffsetDateTime timestamp) {
        this.id = id;
        this.moisture = moisture;
        this.percentage = percentage;
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
