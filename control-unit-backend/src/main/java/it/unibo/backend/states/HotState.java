package it.unibo.backend.states;

import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.Temperature;
import it.unibo.backend.Settings.WindowLevel;
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
        if (controlUnit.getMode().equals(OperatingMode.AUTO)) {
            final TemperatureSample sample = controlUnit.getSampler().getLastSample();
            if (sample != null) {
                controlUnit.setFreqMultiplier(FreqMultiplier.INCREASED);
                final double temperature = sample.getTemperature();
                final double mappedValue = ((temperature - Temperature.NORMAL)
                    / (Temperature.HOT - Temperature.NORMAL))
                    * (WindowLevel.FULLY_OPEN - WindowLevel.FULLY_CLOSED)
                    + WindowLevel.FULLY_CLOSED;
                controlUnit.setWindowLevel(mappedValue);
            }
        }
    }

    @Override
    public State next() {
        final TemperatureSample sample = controlUnit.getSampler().getLastSample();
        if (sample != null) {
            if (sample.getTemperature() < Temperature.NORMAL) {
                return new NormalState(controlUnit);
            } else if (sample.getTemperature() < Temperature.HOT) {
                return new HotState(controlUnit); // We do not return "this" because the mapped value could vary at each handle.
            } else {
                return new TooHotState(controlUnit);
            }
        } else {
            return new HotState(controlUnit); // Same here.
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.HOT;
    }
}
