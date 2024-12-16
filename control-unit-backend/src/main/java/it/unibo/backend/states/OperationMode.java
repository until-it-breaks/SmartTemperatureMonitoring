package it.unibo.backend.states;

public enum OperationMode {
    AUTO("auto"),
    MANUAL("manual");

    private String name;

    OperationMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
