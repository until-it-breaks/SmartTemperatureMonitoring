package it.unibo.backend.http.client;

import io.vertx.core.json.JsonObject;

/**
 * The {@code HttpEndpointObserver} interface represents an observer that is notified when an 
 * HTTP message is received from an endpoint.
 * Implementing classes should define how to process or handle the received HTTP message.
 */
public interface HttpEndpointObserver {

    void onHTTPMessageReceived(JsonObject data);

}
