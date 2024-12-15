package it.unibo.backend.mqtt;

import java.util.ArrayList;
import java.util.List;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class BackendMQTTClient extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(BackendMQTTClient.class);
    private static final String CLIENT_NAME = "ControlUnitClient";

    private final String brokerAddress;
    private final int brokerPort;
    private MqttClient client;
    private final List<MQTTMessageObserver> observers = new ArrayList<>();

    public BackendMQTTClient(String brokerAddress, int brokerPort) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
    }

    @Override
    public void start() {
        MqttClientOptions options = new MqttClientOptions();
        options.setClientId(CLIENT_NAME);
        options.setKeepAliveInterval(60);

        client = MqttClient.create(vertx, options);

        client.connect(brokerPort, brokerAddress,  result -> {
            if (result.succeeded()) {
                logger.info("Connected to MQTT broker at: " + brokerAddress);
            } else {
                logger.error("Failed to connect to MQTT broker at: " + brokerAddress);
            }
        });
        client.publishHandler(message -> {
            String topic = message.topicName();
            String payload = message.payload().toString();
            logger.info("Received message of topic [" + topic + "]: " + payload);

            try {
                notifyObservers(topic, new JsonObject(payload));
            } catch (Exception e) {
                logger.error("Failed to parse payload as JSON: " + payload, e);
            }
        });
    }

    public void subscribe(String topic) {
        client.subscribe(topic, MqttQoS.AT_LEAST_ONCE.value(), result -> {
            if (result.succeeded()) {
                logger.info("Subscribed to topic: [" + topic + "]");
            } else {
                logger.error("Failed to subscribe to topic [" + topic + "]: " + result.cause());
            }
        });
    }

    public void publishMessage(String topic, String key, String messageContent) {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.put(key, messageContent);
        logger.info("Publishing message to [" + topic + "]: " + jsonMessage);
        client.publish(topic, Buffer.buffer(jsonMessage.encode()), MqttQoS.AT_LEAST_ONCE, false, false);
    }

    // Observer pattern

    public void addObserver(MQTTMessageObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MQTTMessageObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String topic, JsonObject jsonPayload) {
        for (MQTTMessageObserver observer : observers) {
            observer.onMessageReceived(topic, jsonPayload);
        }
    }
}
