package it.unibo.backend.states;

import it.unibo.backend.controlUnit.ControlUnit;
import it.unibo.backend.util.ControlUnitUtil;

public class TooHotState implements SystemState {

    private ControlUnit controlUnit;
    private long timeSinceCreation;

    public TooHotState(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
        this.timeSinceCreation = System.currentTimeMillis();
    }

    @Override
    public void handle() {
        if (controlUnit.getOperationMode().equals(OperationMode.AUTO)) {
            controlUnit.setFrequency(ControlUnitUtil.Frequency.INCREASED);
            controlUnit.setWindowLevel(ControlUnitUtil.ActuatorState.FULLY_OPEN);
        }
    }

    @Override
    public SystemState next() {
        double temperature = controlUnit.getTemperatureSampler().getTemperature();
        if (temperature < ControlUnitUtil.TemperatureThresholds.NORMAL) {
            return new NormalState(this.controlUnit);
        } else if (temperature < ControlUnitUtil.TemperatureThresholds.HOT) {
            return new HotState(this.controlUnit);
        } else if (temperature >= ControlUnitUtil.TemperatureThresholds.TOO_HOT) {
            if (System.currentTimeMillis() - timeSinceCreation > ControlUnitUtil.TemperatureThresholds.TOO_HOT_WINDOW) {
                return new AlarmState(controlUnit);
            } else {
                return this;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return "TOO_HOT";
    }
}
