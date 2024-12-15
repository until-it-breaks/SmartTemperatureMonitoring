package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;

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
            controlUnit.setFrequency(1.5);
            controlUnit.setWindowLevel(90);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < 10) {
            return new NormalState(this.controlUnit);
        } else if (temperature < 20) {
            return new HotState(this.controlUnit);
        } else if (temperature >= 20) {
            if (System.currentTimeMillis() - timeSinceCreation > 5000) {
                return new AlarmState(controlUnit);
            } else {
                return this;
            }
        }
        throw new IllegalStateException();
    }
}
