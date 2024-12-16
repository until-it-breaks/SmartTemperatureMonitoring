package it.unibo.backend.temperature;

public class TemperatureSample {
    private double value;
    private long time;

    public TemperatureSample(double value, long time) {
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}
