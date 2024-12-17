package it.unibo.backend.controlunit;

public enum OperationMode {
    AUTO("auto"),
    MANUAL("manual");

    private final String name;

    OperationMode(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
