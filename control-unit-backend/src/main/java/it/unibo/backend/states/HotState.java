package it.unibo.backend.states;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.controlunit.ControlUnitUtil;
import it.unibo.backend.controlunit.OperationMode;

public class HotState implements SystemState {
    private final ControlUnit controlUnit;

    public HotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.FreqMultiplier.INCREASED);
            final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
            final int mappedValue = (int) (((temperature - ControlUnitUtil.TempThresholds.NORMAL)
                / (ControlUnitUtil.TempThresholds.HOT - ControlUnitUtil.TempThresholds.NORMAL))
                * (ControlUnitUtil.DoorState.FULLY_OPEN - ControlUnitUtil.DoorState.FULLY_CLOSED)
                + ControlUnitUtil.DoorState.FULLY_CLOSED);
            controlUnit.setWindowLevel(mappedValue);
        }
    }

    @Override
    public SystemState next() {
        final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
        if (temperature < ControlUnitUtil.TempThresholds.NORMAL) {
            return new NormalState(controlUnit);
        } else if (temperature < ControlUnitUtil.TempThresholds.HOT) {
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
