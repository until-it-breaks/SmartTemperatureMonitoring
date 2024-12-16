package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TemperatureReport {
    private long startTime;
    private long endTime;

    private double average;
    private double min;
    private double max;

    public TemperatureReport() {
    }

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

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "TemperatureReport [from " + startTime + " to " 
            + endTime + ", average=" + average + ", min=" 
            + min + ", max=" + max + "]";
    }

    public String toStringSimple() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            return "TemperatureReport [from " + timeFormat.format(new Date(startTime)) + " to " 
                + timeFormat.format(new Date(endTime)) + String.format(" Average= %.2f, Min= %.2f, Max= %.2f]", average, min, max);
        }
}
