package it.unibo.backend.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class TestMQTTClient {
    private static final Logger logger = LoggerFactory.getLogger(TestMQTTClient.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        MQTTClient client = new MQTTClient("34.154.239.184", 1883);
        vertx.deployVerticle(client, result -> {
            if (result.succeeded()) {
                logger.info("MQTTClient deployed successfully");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.subscribe("esiot");
                JsonObject data = new JsonObject();
                data.put("welcomeMessage", "ciao");
                client.publish("esiot", data);
            } else {
                logger.error("MQTTClient deployment failed: " + result.cause());
            }
        });
    }
}
