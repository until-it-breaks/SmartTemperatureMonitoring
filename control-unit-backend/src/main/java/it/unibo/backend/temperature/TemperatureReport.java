package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.JsonUtility;

/**
 * A container for aggregate temperature information between two timestamps.
 */
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + ((average == null) ? 0 : average.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TemperatureReport other = (TemperatureReport) obj;
        if (startTime != other.startTime)
            return false;
        if (endTime != other.endTime)
            return false;
        if (average == null) {
            if (other.average != null)
                return false;
        } else if (!average.equals(other.average))
            return false;
        if (min == null) {
            if (other.min != null)
                return false;
        } else if (!min.equals(other.min))
            return false;
        if (max == null) {
            if (other.max != null)
                return false;
        } else if (!max.equals(other.max))
            return false;
        return true;
    }
}
