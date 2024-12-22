package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.JsonUtility;

public class TemperatureReport {
    private long startTime;
    private long endTime;

    private Double average;
    private Double min;
    private Double max;

    public TemperatureReport() {
    }

    public TemperatureReport(final long startTime, final long endTime, final Double average, final Double min, final Double max) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.average = average;
        this.min = min;
        this.max = max;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public Double getAverage() {
        return this.average;
    }

    public Double getMin() {
        return this.min;
    }

    public Double getMax() {
        return this.max;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public void setAverage(final Double average) {
        this.average = average;
    }

    public void setMin(final Double min) {
        this.min = min;
    }

    public void setMax(final Double max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "TemperatureReport [from " + this.startTime + " to " 
            + this.endTime + ", average=" + this.average + ", min=" 
            + this.min + ", max=" + this.max + "]";
    }

    public String toStringSimple() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return "TemperatureReport [from " + timeFormat.format(new Date(this.startTime)) + " to " 
            + timeFormat.format(new Date(this.endTime))
            + String.format(" Average= %.2f, Min= %.2f, Max= %.2f]"
            , this.average, this.min, this.max);
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
