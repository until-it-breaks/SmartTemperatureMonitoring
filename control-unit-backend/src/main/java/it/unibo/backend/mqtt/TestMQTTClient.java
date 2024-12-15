package it.unibo.backend.mqtt;

import io.vertx.core.Vertx;

public class TestMQTTClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        MQTTAgent agent = new MQTTAgent();
        vertx.deployVerticle(agent);
    }
}
