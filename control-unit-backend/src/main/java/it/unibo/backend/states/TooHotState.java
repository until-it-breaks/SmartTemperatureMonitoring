package it.unibo.backend.states;

import it.unibo.backend.Settings;
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
            controlUnit.setFreqMultiplier(Settings.FreqMultiplier.INCREASED);
            controlUnit.setWindowLevel(Settings.WindowLevel.FULLY_OPEN);
        }
    }

    @Override
    public State next() {
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (sample.getValue() < Settings.Temperature.NORMAL) {
                return new NormalState(this.controlUnit);
            } else if (sample.getValue() < Settings.Temperature.HOT) {
                return new HotState(this.controlUnit);
            } else {
                if (System.currentTimeMillis() - timeSinceCreation > Settings.Temperature.TOO_HOT_WINDOW) {
                    return new AlarmState(controlUnit);
                } else {
                    return this;
                }
            }
        } else {
            return this;
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.TOO_HOT;
    }
}
