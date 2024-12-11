package it.unibo.backend;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;

public class MQTTAgent extends AbstractVerticle {
    private static final String BROKER_ADDRESS = "34.154.239.184";
    private static final String TOPIC_NAME = "esiot-2024";

    public MQTTAgent() {
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);
        client.connect(1883, BROKER_ADDRESS, c-> {
            log("connected");

            log("subscribing...");
            client.publishHandler(s -> {
                System.out.println("There are new message in topic: " + s.topicName());
                System.out.println("Content of the message: " + s.payload().toString());
                System.out.println("QoS: " + s.qosLevel());
            })
            .subscribe(TOPIC_NAME, 2);

            log("publishing a msg");
            client.publish(TOPIC_NAME, Buffer.buffer("hello"), MqttQoS.AT_LEAST_ONCE, false, false);
        });
    }

    private void log(String msg) {
		System.out.println("[MQTT AGENT] "+msg);
	}
}
