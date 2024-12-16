package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitUtil;

public class NormalState implements SystemState {

    private ControlUnit controlUnit;

    public NormalState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.Frequency.NORMAL);
            controlUnit.setWindowLevel(ControlUnitUtil.ActuatorState.FULLY_CLOSED);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitUtil.TemperatureThresholds.NORMAL) {
            return this;
        } else if (temperature < ControlUnitUtil.TemperatureThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else {
            return new TooHotState(this.controlUnit);
        }
    }

    @Override
    public String getName() {
        return "NORMAL";
    }
}
