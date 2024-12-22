package it.unibo.backend;

import io.vertx.core.Vertx;
import it.unibo.backend.http.server.HttpService;

public class RunHttpService {

    /*
     * Start the HTTP server
     */
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpService service = new HttpService(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT);
        vertx.deployVerticle(service);
    }
}
