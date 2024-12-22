package it.unibo.backend.controlunit.managers;

import it.unibo.backend.controlunit.ControlUnitData;

/**
 * Classes implementing this interface will send updates to endpoints using
 * data sampled from the Control Unit.
 */
public interface UpdateManager {
    void sendUpdate(ControlUnitData data);
}
