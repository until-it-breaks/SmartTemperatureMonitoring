package it.unibo.backend.mqtt;

public enum Topic {
    TEMPERATURE("temperature", "temp"),
    FREQUENCY("frequency", "freq");

    private String name;
    private String key;

    Topic(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }
}
