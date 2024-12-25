package it.unibo.backend.mqtt;

import io.vertx.core.json.JsonObject;

/**
 * The {@code MQTTMessageObserver} interface represents an observer that is notified when an 
 * MQTT message is received from an endpoint.
 * Implementing classes should define how to process or handle the received MQT message.
 */
public interface MQTTMessageObserver {
    void onMQTTMessageReceived(String topic, JsonObject jsonPayload);
}
