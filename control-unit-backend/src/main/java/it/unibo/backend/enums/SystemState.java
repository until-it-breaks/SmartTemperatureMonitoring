package it.unibo.backend.enums;

/**
 * Contains system states.
 */
public enum SystemState {
    NORMAL("normal"),
    HOT("hot"),
    TOO_HOT("too_hot"),
    ALARM("alarm");

    private final String name;

    SystemState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
