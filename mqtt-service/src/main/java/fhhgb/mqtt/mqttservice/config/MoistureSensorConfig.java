package fhhgb.mqtt.mqttservice.config;

public class MoistureSensorConfig {

    /**
     * Dry: (520 430] max 680
     * Wet: (430 350]
     * Water: (350 260] min 260
     */
    public static double getMoisturePercentage(double moisture){
        return 1-((moisture-260)/420);
    }
}
