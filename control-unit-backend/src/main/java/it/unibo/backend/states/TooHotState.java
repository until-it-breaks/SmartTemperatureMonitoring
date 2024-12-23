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
    private final long lastTime;

    public TooHotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
        this.lastTime = System.currentTimeMillis();
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
        if (System.currentTimeMillis() - lastTime > Temperature.TOO_HOT_WINDOW) {
            return new AlarmState(controlUnit);
        }
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (sample.getTemperature() < Temperature.NORMAL) {
                return new NormalState(controlUnit);
            }
            if (sample.getTemperature() < Temperature.HOT) {
                return new HotState(controlUnit);
            }
        }
        return this; // Only case "this" is used since knowing when this state was created is needed.
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.TOO_HOT;
    }
}
