package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.http.JsonUtility;

public class TemperatureReport {
    private long startTime;
    private long endTime;

    private double average;
    private double min;
    private double max;

    public TemperatureReport() {
    }

    public TemperatureReport(final long startTime, final long endTime, final double average, final double min, final double max) {
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

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public void setAverage(final double average) {
        this.average = average;
    }

    public void setMin(final double min) {
        this.min = min;
    }

    public void setMax(final double max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "TemperatureReport [from " + startTime + " to " 
            + endTime + ", average=" + average + ", min=" 
            + min + ", max=" + max + "]";
    }

    public String toStringSimple() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return "TemperatureReport [from " + timeFormat.format(new Date(startTime)) + " to " 
            + timeFormat.format(new Date(endTime)) + String.format(" Average= %.2f, Min= %.2f, Max= %.2f]", average, min, max);
    }

    public JsonObject asJson() {
        final JsonObject data = new JsonObject();
        data.put(JsonUtility.START_TIME, this.startTime);
        data.put(JsonUtility.END_TIME, this.endTime);
        data.put(JsonUtility.AVG_TEMP, this.average);
        data.put(JsonUtility.MIN_TEMP, this.min);
        data.put(JsonUtility.MAX_TEMP, this.max);
        return data;
    }
}
