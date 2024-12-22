package it.unibo.backend.enums;

/**
 * Contains MQTT topics.
 */
public enum Topic {
    TEMPERATURE("temperature"),
    FREQUENCY("frequency");

    private final String name;

    Topic(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
