package it.unibo.backend.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * Unrelated to the backend. Just a MQTT test class.
 */
public class TestMQTTClient {
    public static final Logger logger = LoggerFactory.getLogger(TestMQTTClient.class);

    public static void main(String[] args) {
        MQTTClient client = new MQTTClient("34.154.239.184", 1883);
        client.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JsonObject data = new JsonObject();
        data.put("welcomeMessage", "ciao");
        client.publish("esiot", data);
    }
}
