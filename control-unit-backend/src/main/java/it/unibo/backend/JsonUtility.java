package it.unibo.backend;

/**
 * A utiliy class containing json keywords related to the system.
 */
public class JsonUtility {

    private JsonUtility() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String INTERVENTION_NEED = "requireIntervention";
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
