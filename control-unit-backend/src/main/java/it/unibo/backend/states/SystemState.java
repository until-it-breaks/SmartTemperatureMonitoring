package it.unibo.backend.states;

public interface SystemState {

    void handle();

    SystemState next();

    String getName();
}