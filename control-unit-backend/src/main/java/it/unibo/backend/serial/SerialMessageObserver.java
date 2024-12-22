package it.unibo.backend.serial;

import io.vertx.core.json.JsonObject;

/**
 * The {@code SerialMessageObserver} interface represents an observer that is notified when a 
 * serial message is received from the Arduino R3 Uno.
 * Implementing classes should define how to process or handle the received serial message.
 */
public interface SerialMessageObserver {
    void onSerialMessageReceived(JsonObject message);
}
