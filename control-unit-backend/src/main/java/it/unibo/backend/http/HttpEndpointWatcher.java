package it.unibo.backend.http;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * A daemon that watches over the http server over a specific endpoint and alerts its subscribers.
 */
public class HttpEndpointWatcher {
    private static final Logger logger = LoggerFactory.getLogger(HttpEndpointWatcher.class);

    private final List<HttpEndpointObserver> observers = new ArrayList<>();
    private final String host;
    private final int port;
    private final String path;
    private JsonObject lastData;
    private final Vertx vertx;
    private final WebClient client;

    public HttpEndpointWatcher(final String host, final int port, final String path) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.lastData = null;
        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    /*
     * Performs GET request periodically at the specified interval.
     */
    public void start(final long interval) {
        vertx.setPeriodic(interval, timerId -> {
            client.get(port, host, path).send(ar -> {
                if (ar.succeeded()) {
                    final JsonObject currentData = ar.result().bodyAsJsonObject();
                    if (!currentData.equals(lastData)) {
                        lastData = currentData;
                        notifyObservers(currentData);
                    }
                } else {
                    logger.error("Failed to fetch data: {}", ar.cause().getMessage());
                }
            });

        });
    }

    public void registerObserver(final HttpEndpointObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(final HttpEndpointObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(final JsonObject data) {
        for (final HttpEndpointObserver observer : observers) {
            observer.onHTTPMessageReceived(data);;
        }
    }
}
