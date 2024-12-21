package it.unibo.backend.states;

import it.unibo.backend.Settings;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.temperature.TemperatureSample;

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
            final TemperatureSample sample = controlUnit.getTemperatureSampler().getTemperature();
            if (sample != null) {
                if (sample.getValue() < Settings.Temperature.NORMAL) {
                    return new NormalState(this.controlUnit);
                } else if (sample.getValue() < Settings.Temperature.HOT) {
                    return new HotState(this.controlUnit);
                } else {
                    return new TooHotState(controlUnit);
                }
            } else {
                return this;
            }
        }
    }

    @Override
    public String getName() {
        return SystemStateEnum.ALARM.getName();
    }
}
