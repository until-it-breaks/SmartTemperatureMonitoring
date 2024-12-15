package it.unibo.backend.states;

public interface SystemState {
    void handle();
    void next(SystemState state);
}
