package fhhgb.mqtt.mqttservice.config;

public class MoistureSensorConfig {
    /**
     * Calculates Moisture Percentage
     * Boundaries:
     *  Dry: 4095
     *  Water: 2200
     */
    public static double getMoisturePercentage(double moisture){
        return 1-((moisture-2200)/1895);
    }
}
