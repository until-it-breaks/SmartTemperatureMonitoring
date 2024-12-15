package it.unibo.backend.http;

import io.vertx.core.Vertx;

public class RunService {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DataService service = new DataService(8080);
        vertx.deployVerticle(service);
    }
}
