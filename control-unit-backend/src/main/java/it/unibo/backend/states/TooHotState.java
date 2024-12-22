package it.unibo.backend.states;

import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.Temperature;
import it.unibo.backend.Settings.WindowLevel;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureSample;

public class TooHotState implements State {
    private final ControlUnit controlUnit;
    private final long timeSinceCreation;

    public TooHotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
        this.timeSinceCreation = System.currentTimeMillis();
    }

    @Override
    public void handle() {
        if (controlUnit.getMode().equals(OperatingMode.AUTO)) {
            controlUnit.setFreqMultiplier(FreqMultiplier.INCREASED);
            controlUnit.setWindowLevel(WindowLevel.FULLY_OPEN);
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
                if (System.currentTimeMillis() - timeSinceCreation > Temperature.TOO_HOT_WINDOW) {
                    return new AlarmState(controlUnit);
                } else {
                    return new TooHotState(controlUnit);
                }
            }
        } else {
            return new TooHotState(controlUnit);
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.TOO_HOT;
    }
}
