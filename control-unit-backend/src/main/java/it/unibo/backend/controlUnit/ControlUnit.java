package it.unibo.backend.controlUnit;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.mqtt.BackendMQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.mqtt.Topic;
import it.unibo.backend.serial.SerialCommChannel;
import it.unibo.backend.serial.SerialMessageObserver;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.OperationMode;
import it.unibo.backend.states.SystemState;
import it.unibo.backend.temperature.TemperatureSampler;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver {

    private TemperatureSampler temperatureSampler;
    private double frequency;
    private SerialCommChannel commChannel;
    private BackendMQTTClient mqttClient;
    private OperationMode operationMode;
    private double windowLevel;
    private SystemState currentState;
    private boolean needIntervention;

    public ControlUnit(TemperatureSampler temperatureSampler, SerialCommChannel commChannel, BackendMQTTClient mqttClient) {
        this.temperatureSampler = temperatureSampler;
        this.commChannel = commChannel;
        this.mqttClient = mqttClient;
        this.frequency = 0;
        this.needIntervention = false;
        this.currentState = new NormalState(this);

        this.mqttClient.addObserver(this);
        this.commChannel.addObserver(this);
    }

    public void start() {
        currentState.handle();
        while (true) {
            // check http for permission to exit alarm mode
            // check http for operation mode
            SystemState nexState = currentState.next();
            if (nexState != currentState) {
                currentState = nexState;
                currentState.handle();
            }
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

    @Override
    public void onMessageReceived(JsonObject message) {
        switch (message.getString("operationMode")) {
            case "AUTO":
                this.operationMode = OperationMode.AUTO;
                break;
            case "MANUAL":
                this.operationMode = OperationMode.MANUAL;
                break;
            default:
                throw new IllegalStateException();
        }
        if (this.operationMode.equals(OperationMode.MANUAL)) {
            this.windowLevel = message.getDouble("windowLevel");
        }
    }
}
