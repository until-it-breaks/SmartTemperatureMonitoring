package it.unibo.backend.states;

import it.unibo.backend.Settings.Temperature;
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
            return new AlarmState(controlUnit);
        } else {
            final TemperatureSample sample = controlUnit.getSampler().getLastSample();
            if (sample != null) {
                if (sample.getTemperature() < Temperature.NORMAL) {
                    return new NormalState(controlUnit);
                } else if (sample.getTemperature() < Temperature.HOT) {
                    return new HotState(controlUnit);
                } else {
                    return new TooHotState(controlUnit);
                }
            } else {
                return new AlarmState(controlUnit);
            }
        }
    }

    @Override
    public SystemState getStateAlias() {
        return SystemState.ALARM;
    }
}
