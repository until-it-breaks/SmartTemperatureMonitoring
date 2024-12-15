package it.unibo.backend.controlUnit;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.mqtt.BackendMQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.mqtt.Topic;
import it.unibo.backend.serial.CommChannel;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.OperationMode;
import it.unibo.backend.states.SystemState;
import it.unibo.backend.temperature.TemperatureSampler;

public class ControlUnit implements MQTTMessageObserver {

    private TemperatureSampler temperatureSampler;
    private double frequency;
    private CommChannel commChannel;
    private BackendMQTTClient mqttClient;
    private OperationMode operationMode;
    private double windowLevel;
    private SystemState state;
    private boolean needIntervention;

    public ControlUnit(TemperatureSampler temperatureSampler, CommChannel commChannel, BackendMQTTClient mqttClient) {
        this.temperatureSampler = temperatureSampler;
        this.commChannel = commChannel;
        this.mqttClient = mqttClient;
        this.frequency = 0;
        this.needIntervention = false;
        this.state = new NormalState(this);
    }

    public void start() {
        while (true) {
            // check http for permission to exit alarm mode
            // check serial buffer for operation mode
            // check http for operation mode
            state.handle();
            state = state.next();
            System.out.println("Send via HTTP: temp readings buffer, avg/min/max history, state, window open level");
            commChannel.sendMsg(String.format("Level: %.2f|Mode: %s|Temp: $.2f", windowLevel, operationMode, temperatureSampler.getTemperature()));
        }
    }

    public TemperatureSampler getTemperatureSampler() {
        return this.temperatureSampler;
    }

    public OperationMode getOperationMode() {
        return this.operationMode;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setWindowLevel(int level) {
        this.windowLevel = level;
    }

    public void setNeedForIntervention(boolean needsIntervention) {
        this.needIntervention = needsIntervention;
    }

    public boolean needsIntervention() {
        return this.needIntervention;
    }

    @Override
    public void onMessageReceived(String topic, JsonObject jsonPayload) {
        if (topic.equals(Topic.TEMPERATURE.getName())) {
            double temperature = jsonPayload.getDouble(Topic.TEMPERATURE.getKey());
            this.temperatureSampler.addReading(System.currentTimeMillis(), temperature);
        }
    }
}
