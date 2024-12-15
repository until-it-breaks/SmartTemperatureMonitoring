package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitConfig;

public class HotState implements SystemState {

    private ControlUnit controlUnit;

    public HotState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitConfig.Frequency.INCREASED);
            double temperature = controlUnit.getTemperatureSampler().getTemperature();
            int mappedValue = (int) (((temperature - ControlUnitConfig.TemperatureThresholds.NORMAL)
                / (ControlUnitConfig.TemperatureThresholds.HOT - ControlUnitConfig.TemperatureThresholds.NORMAL))
                * (ControlUnitConfig.ActuatorState.FULLY_OPEN - ControlUnitConfig.ActuatorState.FULLY_CLOSED)
                + ControlUnitConfig.ActuatorState.FULLY_CLOSED);
            controlUnit.setWindowLevel(mappedValue);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitConfig.TemperatureThresholds.NORMAL) {
            return new NormalState(controlUnit);
        } else if (temperature < ControlUnitConfig.TemperatureThresholds.HOT) {
            return this;
        } else {
            return new TooHotState(this.controlUnit);
        }
    }
    
}
