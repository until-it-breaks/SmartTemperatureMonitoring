package it.unibo.backend.states;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.controlunit.ControlUnitConfig;

public class AlarmState implements SystemState {
    private final ControlUnit controlUnit;

    public AlarmState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        this.controlUnit.setNeedForIntervention(true);
    }

    @Override
    public SystemState next() {
        if (controlUnit.needsIntervention()) {
            return this;
        } else {
            final double temperature = controlUnit.getTemperatureSampler().getTemperature().getValue();
            if (temperature < ControlUnitConfig.TempThresholds.NORMAL) {
                return new NormalState(this.controlUnit);
            } else if (temperature < ControlUnitConfig.TempThresholds.HOT) {
                return new HotState(this.controlUnit);
            } else {
                return new TooHotState(controlUnit);
            }
        }
    }

    @Override
    public String getName() {
        return SystemStateEnum.ALARM.getName();
    }
}
