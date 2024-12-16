package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitConfig;

public class AlarmState implements SystemState {

    private ControlUnit controlUnit;

    public AlarmState(ControlUnit controlUnit) {
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
            double temperature = controlUnit.getTemperatureSampler().getTemperature();
            if (temperature < ControlUnitConfig.TemperatureThresholds.NORMAL) {
                return new NormalState(this.controlUnit);
            } else if (temperature < ControlUnitConfig.TemperatureThresholds.HOT) {
                return new HotState(this.controlUnit);
            } else {
                return new TooHotState(controlUnit);
            }
        }
    }

    @Override
    public String getName() {
        return "ALARM";
    }
}
