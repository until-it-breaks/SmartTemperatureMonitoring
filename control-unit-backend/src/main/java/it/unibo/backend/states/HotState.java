package it.unibo.backend.states;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.controlunit.ControlUnitConfig;
import it.unibo.backend.enums.OperationMode;

public class HotState implements SystemState {
    private final ControlUnit controlUnit;

    public HotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitConfig.FreqMultiplier.INCREASED);
            final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
            final int mappedValue = (int) (((temperature - ControlUnitConfig.TempThresholds.NORMAL)
                / (ControlUnitConfig.TempThresholds.HOT - ControlUnitConfig.TempThresholds.NORMAL))
                * (ControlUnitConfig.DoorState.FULLY_OPEN - ControlUnitConfig.DoorState.FULLY_CLOSED)
                + ControlUnitConfig.DoorState.FULLY_CLOSED);
            controlUnit.setWindowLevel(mappedValue);
        }
    }

    @Override
    public SystemState next() {
        final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
        if (temperature < ControlUnitConfig.TempThresholds.NORMAL) {
            return new NormalState(controlUnit);
        } else if (temperature < ControlUnitConfig.TempThresholds.HOT) {
            return this;
        } else {
            return new TooHotState(this.controlUnit);
        }
    }

    @Override
    public String getName() {
        return SystemStateEnum.HOT.getName();
    }
}
