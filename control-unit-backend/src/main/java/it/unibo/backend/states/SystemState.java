package it.unibo.backend.states;

/**
 * State pattern interface
 */
public interface SystemState {
    /**
     * Performs its internal logic.
     */
    void handle();

    /**
     * 
     * @return the next logical state
     */
    SystemState next();

    /**
     * 
     * @return the state alias
     */
    String getName();
}
