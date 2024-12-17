package it.unibo.backend.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final String host;
    private final int port;

    private final Vertx vertx;
    private final WebClient client;

    public HttpClient(final String host, final int port) {
        this.host = host;
        this.port = port;
        vertx = Vertx.vertx();
        client = WebClient.create(vertx);
    }

    public void sendHttpData(final String uri, final JsonObject data) {
        client.post(port, host, uri).sendJson(data, ar -> {
            if (!ar.succeeded()) {
                logger.error("Failed to send data: {}", ar.cause().getMessage());
            }
        });
    }
}
