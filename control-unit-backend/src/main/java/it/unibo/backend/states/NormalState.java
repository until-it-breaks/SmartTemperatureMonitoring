package it.unibo.backend.states;

import it.unibo.backend.Settings;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureSample;

public class NormalState implements State {
    private final ControlUnit controlUnit;

    public NormalState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperatingMode.AUTO)) {
            controlUnit.setFrequency(Settings.FreqMultiplier.NORMAL);
            controlUnit.setWindowLevel(Settings.DoorState.FULLY_CLOSED);
        }
    }

    @Override
    public State next() {
        final TemperatureSample sample = controlUnit.getTemperatureSampler().getTemperature();
        if (sample != null) {
            if (sample.getValue() < Settings.Temperature.NORMAL) {
                return this;
            } else if (sample.getValue() < Settings.Temperature.HOT) {
                return new HotState(this.controlUnit);
            } else {
                return new TooHotState(this.controlUnit);
            }
        } else {
            return this;
        }
    }

    @Override
    public String getName() {
        return SystemState.NORMAL.getName();
    }
}
