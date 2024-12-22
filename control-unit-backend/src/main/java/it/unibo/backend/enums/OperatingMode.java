package it.unibo.backend.enums;

/**
 * Contains the window operation modes.
 */
public enum OperatingMode {
    AUTO("auto", 0),
    MANUAL("manual", 1);

    private final String name;  // Used for http
    private final int value;    // Used for serial comms for better efficiency

    OperatingMode(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }
}
