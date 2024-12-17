package it.unibo.backend.enums;

public enum MQTTTopic {
    TEMPERATURE("temperature"),
    FREQUENCY("frequency");

    private final String name;

    MQTTTopic(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
