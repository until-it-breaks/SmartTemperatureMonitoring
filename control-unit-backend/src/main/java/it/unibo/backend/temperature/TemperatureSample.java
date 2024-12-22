package it.unibo.backend.temperature;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.JsonUtility;

public class TemperatureSample {
    private final double value;
    private final long time;

    public TemperatureSample(final double value, final long time) {
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    public JsonObject asJson() {
        JsonObject data = new JsonObject();
        data.put(JsonUtility.TEMPERATURE, this.value);
        data.put(JsonUtility.SAMPLE_TIME, this.time);
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TemperatureSample other = (TemperatureSample) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        if (time != other.time)
            return false;
        return true;
    }
}
