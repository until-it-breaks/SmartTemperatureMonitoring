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
}
