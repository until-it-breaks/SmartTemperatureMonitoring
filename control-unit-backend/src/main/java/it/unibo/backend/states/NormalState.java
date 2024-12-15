package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;

public class NormalState implements SystemState {

    private ControlUnit controlUnit;

    public NormalState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(1);
            controlUnit.setWindowLevel(0);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < 10) {
            return this;
        } else if (temperature < 20) {
            return new HotState(this.controlUnit);
        } else {
            return new TooHotState(this.controlUnit);
        }
    }
}
