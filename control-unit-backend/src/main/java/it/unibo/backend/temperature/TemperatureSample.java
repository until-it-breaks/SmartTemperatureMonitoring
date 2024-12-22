package it.unibo.backend.temperature;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.JsonUtility;

/**
 * A container that holds a temperature reading and its timestamp.
 */
public class TemperatureSample {
    private final double temperature;
    private final long timeStamp;

    public TemperatureSample(final double temperature, final long timeStamp) {
        this.temperature = temperature;
        this.timeStamp = timeStamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public JsonObject asJson() {
        final JsonObject data = new JsonObject();
        data.put(JsonUtility.TEMPERATURE, this.temperature);
        data.put(JsonUtility.SAMPLE_TIME, this.timeStamp);
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(temperature);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
        final TemperatureSample other = (TemperatureSample) obj;
        if (Double.doubleToLongBits(temperature) != Double.doubleToLongBits(other.temperature))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }
}
