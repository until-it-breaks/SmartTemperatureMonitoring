package it.unibo.backend;

import io.vertx.core.Vertx;
import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.http.server.HttpService;

public class RunHttpService {

    /*
     * Start the HTTP server
     */
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpService service = new HttpService(Connectivity.SERVER_HOST_LOCAL, Connectivity.SERVER_PORT);
        vertx.deployVerticle(service);
    }
}
