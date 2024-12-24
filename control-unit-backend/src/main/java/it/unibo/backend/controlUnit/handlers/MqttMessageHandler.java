package it.unibo.backend.controlunit.handlers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.temperature.TemperatureSampler;

public class MqttMessageHandler implements MessageHandler {

    private final TemperatureSampler sampler;

    public MqttMessageHandler(final TemperatureSampler sampler) {
        this.sampler = sampler;
    }

    @Override
    public void handleMessage(final JsonObject data) {
        final double temperature = data.getDouble(JsonUtility.TEMPERATURE);
        sampler.addSample(System.currentTimeMillis(), temperature);
    }
}
