package it.unibo.backend.states;

/**
 * State pattern interface
 */
public interface State {
    /**
     * Performs its internal logic.
     */
    void handle();

    /**
     * 
     * @return the next logical state
     */
    State next();

    /**
     * 
     * @return the state alias
     */
    String getName();
}
