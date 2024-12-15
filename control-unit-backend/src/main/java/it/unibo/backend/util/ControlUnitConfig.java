package it.unibo.backend.util;

public class ControlUnitConfig {
    public static class Frequency {
        public static final double NORMAL = 1;
        public static final double INCREASED = 1.5;
    }

    public static class TemperatureThresholds {
        public static final double NORMAL = 20;
        public static final double HOT = 25;
        public static final double TOO_HOT = 30;
        public static final long TOO_HOT_WINDOW = 5000;
    }

    public static class ActuatorState {
        public static final int FULLY_CLOSED = 0;
        public static final int FULLY_OPEN = 90;
    }

    private ControlUnitConfig() {
        throw new UnsupportedOperationException("Utility class");
    }
}