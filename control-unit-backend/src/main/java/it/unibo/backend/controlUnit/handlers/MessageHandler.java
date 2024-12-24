package it.unibo.backend.controlunit.handlers;

import io.vertx.core.json.JsonObject;

/**
 * Interface for handling messages received from various communication channels.
 * <p>
 * Implementations of this interface define the logic to process messages
 * in a format-specific or channel-specific manner, such as MQTT, Serial, or HTTP.
 * By using this interface, message handling can be modularized and delegated
 * to separate components, promoting flexibility and maintainability.
 * </p>
 */
public interface MessageHandler {
    void handleMessage(JsonObject data);
}
