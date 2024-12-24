package it.unibo.backend.controller.handlers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.controller.ControlUnit;
import it.unibo.backend.enums.OperatingMode;

public class SerialMessageHandler implements MessageHandler {

    private final ControlUnit controlUnit;

    public SerialMessageHandler(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handleMessage(final JsonObject data) {
        final int modeToSwitchTo = data.getInteger(JsonUtility.REQUESTED_MODE);
        if (modeToSwitchTo == OperatingMode.AUTO.getValue()) {
            controlUnit.setMode(OperatingMode.AUTO);
            System.out.println("Switching to AUTO");
        } else if (modeToSwitchTo == OperatingMode.MANUAL.getValue()) {
            controlUnit.setMode(OperatingMode.MANUAL);
            System.out.println("Switching to MANUAL");
        }
        if (controlUnit.getMode().equals(OperatingMode.MANUAL)) {
            controlUnit.setWindowLevel(data.getDouble(JsonUtility.WINDOW_LEVEL));
        }
    }
}
