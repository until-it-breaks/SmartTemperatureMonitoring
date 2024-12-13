package it.unibo.backend;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

/**
 * Right now this is not worthy of the name "Agent".
 * Will be improved down the line.
 */
public class MQTTAgent extends AbstractVerticle {
    private static final String BROKER_ADDRESS = "34.154.239.184";
    private static final int BROKER_PORT = 1883;
    private static final String TOPIC_NAME = "esiot-2024";
    private MqttClient client;

    private static final Logger logger = LoggerFactory.getLogger(MQTTAgent.class);

    @Override
    public void start() {
        MqttClientOptions options = new MqttClientOptions();
        options.setClientId("ControlUnitClient");
        options.setKeepAliveInterval(60);

        client = MqttClient.create(vertx, options);
        client.connect(BROKER_PORT, BROKER_ADDRESS,  result -> {
            if (result.succeeded()) {
                logger.info("Connected to MQTT broker at: " + BROKER_ADDRESS);
                logger.info("Subscribing...");
                this.subscribeToTopic();
            } else {
                logger.error("Failed to connect to MQTT broker");
            }
        });
    }

    public void subscribeToTopic() {
        client.subscribe(TOPIC_NAME, MqttQoS.AT_LEAST_ONCE.value(), result -> {
            if (result.succeeded()) {
                logger.info("Subscribed to topic: " + TOPIC_NAME);
            } else {
                logger.error("Failed to subscribe to topic: " + result.cause());
            }
        });

        client.publishHandler(message -> {
            logger.info("Received message: " + message.payload().toString());
        });
    }

    public void publishMessage(String messageContent) {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.put("message", messageContent);
        logger.info("Publishing a message");
        client.publish(TOPIC_NAME, Buffer.buffer(jsonMessage.encode()), MqttQoS.AT_LEAST_ONCE, false, false);
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
        agent.publishMessage("ciaone2");
        logger.error("This is an error message!");
    }
}
