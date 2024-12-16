package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitConfig;

public class TooHotState implements SystemState {

    private ControlUnit controlUnit;
    private long timeSinceCreation;

    public TooHotState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
        this.timeSinceCreation = System.currentTimeMillis();
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitConfig.Frequency.INCREASED);
            controlUnit.setWindowLevel(ControlUnitConfig.ActuatorState.FULLY_OPEN);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitConfig.TemperatureThresholds.NORMAL) {
            return new NormalState(this.controlUnit);
        } else if (temperature < ControlUnitConfig.TemperatureThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else if (temperature >= ControlUnitConfig.TemperatureThresholds.TOO_HOT) {
            if (System.currentTimeMillis() - timeSinceCreation > ControlUnitConfig.TemperatureThresholds.TOO_HOT_WINDOW) {
                return new AlarmState(controlUnit);
            } else {
                return this;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return "TOO_HOT";
    }
}
