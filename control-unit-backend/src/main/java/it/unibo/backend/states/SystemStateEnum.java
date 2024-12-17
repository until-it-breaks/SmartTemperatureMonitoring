package it.unibo.backend.states;

public enum SystemStateEnum {
    NORMAL("normal"),
    HOT("hot"),
    TOO_HOT("too hot"),
    ALARM("alarm");

    private final String name;

    SystemStateEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
