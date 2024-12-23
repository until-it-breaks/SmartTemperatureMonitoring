package it.unibo.backend.mqtt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;

/**
 * A client that allows both subscription and publishing when connected to a MQTT broker.
 */
public class MQTTClient extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MQTTClient.class);

    private final String brokerHost;
    private final int brokerPort;
    private final Vertx vertx;
    private final MqttClient client;
    private final List<MQTTMessageObserver> observers;

    public MQTTClient(final String brokerAddress, final int brokerPort) {
        this.brokerHost = brokerAddress;
        this.brokerPort = brokerPort;
        this.observers = new ArrayList<>();
        this.vertx = Vertx.vertx();
        this.client = MqttClient.create(vertx);
    }

    @Override
    public void start() {
        client.connect(brokerPort, brokerHost,  result -> {
            if (result.succeeded()) {
                logger.info("Connected to MQTT broker at: " + brokerHost);
            } else {
                logger.error("Failed to connect to MQTT broker at: " + brokerHost);
            }
        });
        client.publishHandler(message -> {
            final String topic = message.topicName();
            final String payload = message.payload().toString();
            try {
                JsonObject data = new JsonObject(payload);
                notifyObservers(topic, data);
            } catch (final NullPointerException | DecodeException e) {
                logger.error("Failed to parse payload as JSON: {}", e.getMessage());
            }
        });
    }

    public void stop() {
        client.disconnect();
        vertx.close();
    }

    public void subscribe(final String topic) {
        client.subscribe(topic, MqttQoS.AT_LEAST_ONCE.value(), result -> {
            if (result.succeeded()) {
                logger.info("Subscribed to topic: [{}]", topic);
            } else {
                logger.error("Failed to subscribe to topic [" + topic + "]: {}", result.cause());
            }
        });
    }

    public void publish(final String topic, final JsonObject data) {
        logger.info("Publishing message to [{}]: {}", topic, data);
        client.publish(topic, Buffer.buffer(data.encode()), MqttQoS.AT_LEAST_ONCE, false, false);
    }

    public void registerObserver(final MQTTMessageObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(final MQTTMessageObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(final String topic, final JsonObject jsonPayload) {
        for (final MQTTMessageObserver observer : observers) {
            observer.onMQTTMessageReceived(topic, jsonPayload);
        }
    }
}
