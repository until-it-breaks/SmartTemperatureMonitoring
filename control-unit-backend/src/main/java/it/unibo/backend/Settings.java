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
        public static final long TOO_HOT_WINDOW = 4000; // Being in the too hot for 4 seconds will transition to the ALARM state
    }

    public static class WindowLevel {
        public static final double FULLY_CLOSED = 0.0;
        public static final double FULLY_OPEN = 1.0;
    }

    public static class Connectivity {
        // HTTP Server
        public static final String SERVER_HOST_LOCAL = "localhost";
        public static final String SERVER_HOST = "https://hamster-holy-mutt.ngrok-free.app";    // Needed when the HTTP server and backend are not hosted on the same pc
        public static final int SERVER_PORT = 8080;
        // Endpoints used to publish system information
        public static final String TEMPERATURE_PATH = "/api/samples";   // Provides a list of temperature samples (timestamp and temperature)
        public static final String REPORTS_PATH = "/api/reports";       // Provides a list of temperature reports (startTime, endTime and aggregate statistics)   
        public static final String CONFIG_PATH = "/api/config";         // Provides system configurations (windowLevel, status, mode, need for intervention)
        // Special endpoints for requesting backend changes via http
        public static final String SWITCH_MODE_PATH = "/api/request_mode_switch";
        public static final String SWITCH_ALARM_PATH = "/api/request_alarm_switch";
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
        // Temperature sample
        public static final String TEMPERATURE = "temperature";                 // value is a double
        public static final String SAMPLE_TIME = "sampleTime";                  // value is a long
        // Temperature report
        public static final String START_TIME = "startTime";                    // value is a long
        public static final String END_TIME = "endTime";                        // value is a long
        public static final String AVG_TEMP = "averageTemp";                    // value is a double
        public static final String MIN_TEMP = "minimumTemp";                    // value is a double
        public static final String MAX_TEMP = "maximumTemp";                    // value is a double
        // System configuration
        public static final String WINDOW_LEVEL = "windowLevel";                // value is a double
        public static final String FREQ_MULTIPLIER = "frequencyMultiplier";     // value is a double
        public static final String SYSTEM_STATE = "systemState";                // value is a string
        public static final String NEEDS_INTERVENTION = "needsIntervention";    // value is a boolean
        public static final String OPERATING_MODE = "operatingMode";            // value is a string ("auto" or "manual")
        // Request keywords
        public static final String REQUESTED_MODE = "requestedMode";            // must be set to either "auto", "manual" or "none"
        public static final String REQUESTED_ALARM_SWITCH = "switchOffAlarm";   // either true or false
    }
}
