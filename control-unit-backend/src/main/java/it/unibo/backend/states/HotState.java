package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;

public class HotState implements SystemState {

    private ControlUnit controlUnit;

    public HotState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(1.5);
            double temperature = controlUnit.getTemperatureSampler().getTemperature();
            int mappedValue = (int) (((temperature - 10) / (20 - 10)) * (90 - 0) + 0);
            controlUnit.setWindowLevel(mappedValue);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < 10) {
            return new NormalState(controlUnit);
        } else if (temperature < 20) {
            return this;
        } else {
            return new TooHotState(this.controlUnit);
        }
    }
    
}
