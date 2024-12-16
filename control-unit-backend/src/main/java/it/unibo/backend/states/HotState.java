package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitUtil;

public class HotState implements SystemState {

    private ControlUnit controlUnit;

    public HotState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.Frequency.INCREASED);
            double temperature = controlUnit.getTemperatureSampler().getTemperature();
            int mappedValue = (int) (((temperature - ControlUnitUtil.TemperatureThresholds.NORMAL)
                / (ControlUnitUtil.TemperatureThresholds.HOT - ControlUnitUtil.TemperatureThresholds.NORMAL))
                * (ControlUnitUtil.ActuatorState.FULLY_OPEN - ControlUnitUtil.ActuatorState.FULLY_CLOSED)
                + ControlUnitUtil.ActuatorState.FULLY_CLOSED);
            controlUnit.setWindowLevel(mappedValue);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitUtil.TemperatureThresholds.NORMAL) {
            return new NormalState(controlUnit);
        } else if (temperature < ControlUnitUtil.TemperatureThresholds.HOT) {
            return this;
        } else {
            return new TooHotState(this.controlUnit);
        }
    }

    @Override
    public String getName() {
        return "HOT";
    }
    
}
