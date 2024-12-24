package it.unibo.backend.states;

import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.Temperature;
import it.unibo.backend.Settings.WindowLevel;
import it.unibo.backend.controller.ControlUnit;
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
        controlUnit.setFreqMultiplier(FreqMultiplier.NORMAL);
        if (controlUnit.getMode().equals(OperatingMode.AUTO)) {
            controlUnit.setWindowLevel(WindowLevel.FULLY_CLOSED);
        }
    }

    @Override
    public State next() {
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (sample.getTemperature() < Temperature.NORMAL) {
                return new NormalState(controlUnit);
            } else if (sample.getTemperature() < Temperature.HOT) {
                return new HotState(controlUnit);
            } else {
                return new TooHotState(controlUnit);
            }
        } else {
            return new NormalState(controlUnit);
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.NORMAL;
    }
}
