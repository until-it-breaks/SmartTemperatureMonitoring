package it.unibo.backend.temperature;

import java.util.Date;

public class TemperatureReport {
    private long startTime;
    private long endTime;

    private double average;
    private double min;
    private double max;

    public TemperatureReport(long startTime, long endTime, double average, double min, double max) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.average = average;
        this.min = min;
        this.max = max;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public double getAverage() {
        return average;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "TemperatureReport [from " + new Date(startTime) + " to" + new Date(endTime) + ", average=" + average + ", min="
                + min + ", max=" + max + "]";
    }
}
