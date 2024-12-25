package it.unibo.backend.controller.managers;

import it.unibo.backend.controller.ControlUnitData;

/**
 * Classes implementing this interface will send updates to endpoints using
 * data sampled from the Control Unit.
 */
public interface UpdateManager {
    void sendUpdate(ControlUnitData data);
}
