package it.unibo.backend.states;

import it.unibo.backend.enums.SystemState;

/**
 * State pattern interface
 */
public interface State {

    /**
     * Performs its internal logic.
     */
    void handle();

    /**
     * Determines the next state.
     * 
     * @return the next state
     */
    State next();

    /**
     * Returns the state alias.
     * 
     * @return the state alias
     */
    SystemState getStateAlias();
}
