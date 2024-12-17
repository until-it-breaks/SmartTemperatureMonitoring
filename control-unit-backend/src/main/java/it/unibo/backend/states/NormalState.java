package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.controlUnit.ControlUnitUtil;
import it.unibo.backend.controlUnit.OperationMode;

public class NormalState implements SystemState {
    private final ControlUnit controlUnit;

    public NormalState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.FreqMultiplier.NORMAL);
            controlUnit.setWindowLevel(ControlUnitUtil.DoorState.FULLY_CLOSED);
        }
    }

    @Override
    public SystemState next() {
        final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
        if (temperature < ControlUnitUtil.TempThresholds.NORMAL) {
            return this;
        } else if (temperature < ControlUnitUtil.TempThresholds.HOT) {
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
