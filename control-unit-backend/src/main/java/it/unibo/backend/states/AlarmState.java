package it.unibo.backend.states;

import it.unibo.backend.Settings.Temperature;
import it.unibo.backend.controller.ControlUnit;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureSample;

public class AlarmState implements State {
    private final ControlUnit controlUnit;

    public AlarmState(final ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    @Override
    public void handle() {
        // The system is blocked only when operating in AUTO mode.
        if (controlUnit.getMode().equals(OperatingMode.AUTO)) {
            this.controlUnit.setNeedForIntervention(true);
        }
    }

    @Override
    public State next() {
        if (controlUnit.needsIntervention()) {
            return new AlarmState(controlUnit);
        } else {
            final TemperatureSample sample = controlUnit.getSampler().getLastSample();
            if (sample != null) {
                return new NormalState(controlUnit);
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
