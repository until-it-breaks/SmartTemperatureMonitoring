package it.unibo.backend.http.client;

import io.vertx.core.json.JsonObject;

/**
 * The {@code HttpClient} interface defines the contract for sending HTTP data to a remote server.
 * Implementations of this interface are expected to provide the logic for sending JSON data 
 * asynchronously to a specified URI using HTTP POST requests.
 */
public interface HttpClient {

    void sendHttpData(String uri, JsonObject data);

}