package it.unibo.backend.states;

import it.unibo.backend.Settings;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureSample;

public class HotState implements State {
    private final ControlUnit controlUnit;

    public HotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (controlUnit.getMode().equals(OperatingMode.AUTO)) {
                controlUnit.setFreqMultiplier(Settings.FreqMultiplier.INCREASED);
                final double temperature = sample.getValue();
                final double mappedValue = (((temperature - Settings.Temperature.NORMAL)
                    / (Settings.Temperature.HOT - Settings.Temperature.NORMAL))
                    * (Settings.WindowLevel.FULLY_OPEN - Settings.WindowLevel.FULLY_CLOSED)
                    + Settings.WindowLevel.FULLY_CLOSED);
                controlUnit.setWindowLevel(mappedValue);
            }
        }
    }

    @Override
    public State next() {
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (sample.getValue() < Settings.Temperature.NORMAL) {
                return new NormalState(controlUnit);
            } else if (sample.getValue() < Settings.Temperature.HOT) {
                return new HotState(controlUnit);
            } else {
                return new TooHotState(this.controlUnit);
            }
        } else {
            return new HotState(controlUnit);
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.HOT;
    }
}
