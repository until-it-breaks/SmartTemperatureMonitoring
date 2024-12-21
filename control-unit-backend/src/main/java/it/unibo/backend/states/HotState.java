package it.unibo.backend.states;

import it.unibo.backend.Settings;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.OperationMode;
import it.unibo.backend.temperature.TemperatureSample;

public class HotState implements SystemState {
    private final ControlUnit controlUnit;

    public HotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        final TemperatureSample sample = controlUnit.getTemperatureSampler().getTemperature();
        if (sample != null) {
            if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
                controlUnit.setFrequency(Settings.FreqMultiplier.INCREASED);
                final double temperature = sample.getValue();
                final int mappedValue = (int) (((temperature - Settings.Temperature.NORMAL)
                    / (Settings.Temperature.HOT - Settings.Temperature.NORMAL))
                    * (Settings.DoorState.FULLY_OPEN - Settings.DoorState.FULLY_CLOSED)
                    + Settings.DoorState.FULLY_CLOSED);
                controlUnit.setWindowLevel(mappedValue);
            }
        }
    }

    @Override
    public SystemState next() {
        final TemperatureSample sample = controlUnit.getTemperatureSampler().getTemperature();
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
    public String getName() {
        return SystemStateEnum.HOT.getName();
    }
}
