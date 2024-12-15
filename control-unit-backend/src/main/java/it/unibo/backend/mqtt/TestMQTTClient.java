package it.unibo.backend.mqtt;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class TestMQTTClient {
    private static final Logger logger = LoggerFactory.getLogger(TestMQTTClient.class);
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        BackendMQTTClient client = new BackendMQTTClient("34.154.239.184", 1883);
        vertx.deployVerticle(client, result -> {
            if (result.succeeded()) {
                logger.info("MQTTClient deployed successfully");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.subscribe("esiot");
                client.publishMessage("esiot", "key", "ciaone");
                client.publishMessage("ciao", "key", "ciaone2");
            } else {
                logger.error("MQTTClient deployment failed: " + result.cause());
            }
        });
    }
}
