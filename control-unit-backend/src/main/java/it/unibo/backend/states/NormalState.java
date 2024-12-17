package it.unibo.backend.states;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.controlunit.ControlUnitConfig;
import it.unibo.backend.enums.OperationMode;

public class NormalState implements SystemState {
    private final ControlUnit controlUnit;

    public NormalState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitConfig.FreqMultiplier.NORMAL);
            controlUnit.setWindowLevel(ControlUnitConfig.DoorState.FULLY_CLOSED);
        }
    }

    @Override
    public SystemState next() {
        final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
        if (temperature < ControlUnitConfig.TempThresholds.NORMAL) {
            return this;
        } else if (temperature < ControlUnitConfig.TempThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else {
            return new TooHotState(this.controlUnit);
        }
    }

    @Override
    public String getName() {
        return SystemStateEnum.NORMAL.getName();
    }
}
