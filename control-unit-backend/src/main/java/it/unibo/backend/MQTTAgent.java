package it.unibo.backend;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class MQTTAgent extends AbstractVerticle {
    private static final String BROKER_ADDRESS = "34.154.239.184";
    private static final int BROKER_PORT = 1883;
    private static final String TOPIC_NAME = "esiot-2024";
    private MqttClient client;

    @Override
    public void start() {
        MqttClientOptions options = new MqttClientOptions();
        options.setClientId("ControlUnitClient");
        options.setKeepAliveInterval(60);

        client = MqttClient.create(vertx, options);
        client.connect(BROKER_PORT, BROKER_ADDRESS,  result -> {
            if (result.succeeded()) {
                log("Connected to MQTT broker at: " + BROKER_ADDRESS);
                log("subscribing...");
                this.subscribeToTopic();
            } else {
                log("Failed to connect to MQTT broker");
            }
        });
    }

    public void subscribeToTopic() {
        client.subscribe(TOPIC_NAME, MqttQoS.AT_LEAST_ONCE.value(), result -> {
            if (result.succeeded()) {
                log("Subscribed to topic: " + TOPIC_NAME);
            } else {
                log("Failed to subscribe to topic: " + result.cause());
            }
        });

        client.publishHandler(message -> {
            log("Received message: " + message.payload().toString());
        });
    }

    public void publishMessage(String messageContent) {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.put("message", messageContent);
        log("Publishing a message");
        client.publish(TOPIC_NAME, Buffer.buffer(jsonMessage.encode()), MqttQoS.AT_LEAST_ONCE, false, false);
    }

    private void log(String msg) {
		System.out.println("[MQTT AGENT] " + msg);
	}

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        MQTTAgent agent = new MQTTAgent();
        vertx.deployVerticle(agent, result -> {
            if (result.succeeded()) {
                System.out.println("MQTTAgent deployed successfully");
            } else {
                System.out.println("MQTTAgent deployment failed: " + result.cause());
            }
        });
        // Wait for agent to fully deploy.
        Thread.sleep(2000);
        agent.publishMessage("ciaone");
    }
}
