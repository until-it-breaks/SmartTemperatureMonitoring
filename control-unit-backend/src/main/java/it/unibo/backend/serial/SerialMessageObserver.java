package it.unibo.backend.serial;

import io.vertx.core.json.JsonObject;

public interface SerialMessageObserver {
    void onMessageReceived(JsonObject message);
}