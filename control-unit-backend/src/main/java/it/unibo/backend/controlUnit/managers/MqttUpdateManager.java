package it.unibo.backend.controlunit.managers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.JsonUtility;
import it.unibo.backend.controlunit.ControlUnitData;
import it.unibo.backend.enums.Topic;
import it.unibo.backend.mqtt.MQTTClient;

public class MqttUpdateManager implements UpdateManager {
    private final MQTTClient client;
    private double lastFrequencyMultiplier;

    public MqttUpdateManager(final MQTTClient client) {
        this.client = client;
    }

    @Override
    public void sendUpdate(final ControlUnitData data) {
        if (data.getFreqMultiplier() != lastFrequencyMultiplier) {
            final JsonObject jsonData = new JsonObject();
            jsonData.put(JsonUtility.FREQ_MULTIPLIER, data.getFreqMultiplier());
            client.publish(Topic.FREQUENCY.getName(), jsonData);
            lastFrequencyMultiplier = data.getFreqMultiplier();
        }
    }
}
