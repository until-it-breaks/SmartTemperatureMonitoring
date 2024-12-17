package it.unibo.backend.controlUnit;

public class ControlUnitUtil {
    private ControlUnitUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class FreqMultiplier {
        public static final double NORMAL = 1;
        public static final double INCREASED = 1.5;
    }

    public static class TempThresholds {
        public static final double NORMAL = 20;
        public static final double HOT = 25;
        public static final double TOO_HOT = 30;
        public static final long TOO_HOT_WINDOW = 5000;
    }

    public static class DoorState {
        public static final int FULLY_CLOSED = 0;
        public static final int FULLY_OPEN = 90;
    }
}