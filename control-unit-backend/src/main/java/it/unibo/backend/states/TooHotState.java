package it.unibo.backend.states;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.controlunit.ControlUnitUtil;
import it.unibo.backend.controlunit.OperationMode;

public class TooHotState implements SystemState {
    private final ControlUnit controlUnit;
    private final long timeSinceCreation;

    public TooHotState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
        this.timeSinceCreation = System.currentTimeMillis();
    }

    @Override
    public void handle() {
        if (controlUnit.getOperatingMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.FreqMultiplier.INCREASED);
            controlUnit.setWindowLevel(ControlUnitUtil.DoorState.FULLY_OPEN);
        }
    }

    @Override
    public SystemState next() {
        final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
        if (temperature < ControlUnitUtil.TempThresholds.NORMAL) {
            return new NormalState(this.controlUnit);
        } else if (temperature < ControlUnitUtil.TempThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else if (temperature >= ControlUnitUtil.TempThresholds.TOO_HOT) {
            if (System.currentTimeMillis() - timeSinceCreation > ControlUnitUtil.TempThresholds.TOO_HOT_WINDOW) {
                return new AlarmState(controlUnit);
            } else {
                return this;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return SystemStateEnum.TOO_HOT.getName();
    }
}
