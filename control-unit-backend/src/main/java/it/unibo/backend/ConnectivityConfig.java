package it.unibo.backend;

/**
 * Contains connectivity parameters.
 */
public class ConnectivityConfig {

    private ConnectivityConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String SERVER_HOST_LOCAL = "localhost";
    public static final String SERVER_HOST = "https://hamster-holy-mutt.ngrok-free.app";
    public static final int SERVER_PORT = 8080;

    public static final String TEMPERATURE_PATH = "/api/temperature_samples";
    public static final String REPORTS_PATH = "/api/reports";
    public static final String OPERATING_MODE_PATH = "/api/operating_mode";
    public static final String INTERVENTION_PATH = "/api/intervention";
    public static final String CONFIG_PATH = "/api/config";

    public static final String MQTT_BROKER_HOST = "34.154.239.184";
    public static final int MQTT_BROKER_PORT = 1883;

    public static final String DEFAULT_SERIAL_PORT = "COM4";
}
