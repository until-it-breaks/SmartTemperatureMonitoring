package it.unibo.backend;

/**
 * Contains the system parameters.
 */
public class Settings {

    private Settings() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class FreqMultiplier {
        public static final double NORMAL = 1;
        public static final double INCREASED = 1.5;
    }

    public static class Temperature {
        public static final double NORMAL = 20;
        public static final double HOT = 25;
        public static final double TOO_HOT = 30;
        public static final long TOO_HOT_WINDOW = 5000; // Being in the too hot for 5 seconds will transition to the ALARM state
    }

    public static class WindowLevel {
        public static final double FULLY_CLOSED = 0.0;
        public static final double FULLY_OPEN = 1.0;
    }

    public static class Connectivity {
        // HTTP Server
        public static final String SERVER_HOST_LOCAL = "localhost";
        public static final String SERVER_HOST = "https://hamster-holy-mutt.ngrok-free.app";
        public static final int SERVER_PORT = 8080;
        // Endpoint paths
        public static final String TEMPERATURE_PATH = "/api/temperature_samples";
        public static final String REPORTS_PATH = "/api/reports";
        public static final String OPERATING_MODE_PATH = "/api/operating_mode";
        public static final String INTERVENTION_PATH = "/api/intervention";
        public static final String CONFIG_PATH = "/api/config";
        // MQTT
        public static final String MQTT_BROKER_HOST = "34.154.239.184";
        public static final int MQTT_BROKER_PORT = 1883;
        // Serial comms
        public static final String DEFAULT_SERIAL_PORT = "COM4";
    }

    /**
     * Contains JSON keywords
     */
    public class JsonUtility {
        public static final String INTERVENTION_NEED = "needsIntervention";
        public static final String OPERATING_MODE = "operatingMode";
        public static final String TEMPERATURE = "temperature";
        public static final String SAMPLE_TIME = "sampleTime";
        public static final String WINDOW_LEVEL = "windowLevel";
        public static final String FREQ_MULTIPLIER = "frequencyMultiplier";
        public static final String SYSTEM_STATE = "systemState";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String AVG_TEMP = "averageTemp";
        public static final String MIN_TEMP = "minimumTemp";
        public static final String MAX_TEMP = "maximumTemp";
    }
}
