package it.unibo.backend.http;

import io.vertx.core.json.JsonObject;

public interface HttpEndpointObserver {
    void onHTTPMessageReceived(JsonObject message);
}
