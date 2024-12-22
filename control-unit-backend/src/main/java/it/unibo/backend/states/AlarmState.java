package it.unibo.backend.states;

import it.unibo.backend.Settings;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureSample;

public class AlarmState implements State {
    private final ControlUnit controlUnit;

    public AlarmState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        this.controlUnit.setNeedForIntervention(true);
    }

    @Override
    public State next() {
        if (controlUnit.needsIntervention()) {
            return this;
        } else {
            final TemperatureSample sample = controlUnit.getSampler().getLastSample();
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
    public SystemState getStateAlias() {
        return SystemState.ALARM;
    }
}
