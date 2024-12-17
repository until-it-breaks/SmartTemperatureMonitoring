package it.unibo.backend.http;

import io.vertx.core.Vertx;
import it.unibo.backend.Config;

public class RunService {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpService service = new HttpService(Config.SERVER_HOST_LOCAL, Config.SERVER_PORT);
        vertx.deployVerticle(service);
    }
}
