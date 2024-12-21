package it.unibo.backend.enums;

public enum OperationMode {
    AUTO("auto", 0),
    MANUAL("manual", 1);

    private final String name;
    private final int value;

    OperationMode(final String name, final int value) {
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
