package it.unibo.backend.controlunit;

import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

/**
 * Contains data sampled from the control unit.
 */
public class ControlUnitData {
    private final double freqMultiplier;
    private final OperatingMode mode;
    private final double windowLevel;
    private final boolean interventionRequired;
    private final SystemState state;
    private final TemperatureSample sample;
    private final TemperatureReport report;

    public ControlUnitData(final double freqMultiplier, final OperatingMode mode, final double windowLevel, final boolean interventionRequired,
            final SystemState state, final TemperatureSample sample, final TemperatureReport report) {
        this.freqMultiplier = freqMultiplier;
        this.mode = mode;
        this.windowLevel = windowLevel;
        this.interventionRequired = interventionRequired;
        this.state = state;
        this.sample = sample;
        this.report = report;
    }

    public double getFreqMultiplier() {
        return freqMultiplier;
    }

    public OperatingMode getMode() {
        return mode;
    }

    public double getWindowLevel() {
        return windowLevel;
    }

    public boolean isInterventionRequired() {
        return interventionRequired;
    }

    public SystemState getState() {
        return state;
    }

    public TemperatureSample getSample() {
        return sample;
    }

    public TemperatureReport getReport() {
        return report;
    }
}
