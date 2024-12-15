package it.unibo.backend.http;

public class DataPoint {
    private double value;
    private long time;

    public DataPoint(double value, long time) {
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
