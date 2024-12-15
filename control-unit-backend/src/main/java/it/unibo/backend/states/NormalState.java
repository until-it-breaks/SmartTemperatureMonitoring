package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitConfig;

public class NormalState implements SystemState {

    private ControlUnit controlUnit;

    public NormalState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitConfig.Frequency.NORMAL);
            controlUnit.setWindowLevel(ControlUnitConfig.ActuatorState.FULLY_CLOSED);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitConfig.TemperatureThresholds.NORMAL) {
            return this;
        } else if (temperature < ControlUnitConfig.TemperatureThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else {
            return new TooHotState(this.controlUnit);
        }
    }
}
