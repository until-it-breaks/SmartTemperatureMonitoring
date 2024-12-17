package it.unibo.backend.controlunit;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.http.HttpClient;
import it.unibo.backend.http.HttpEndpointObserver;
import it.unibo.backend.http.HttpEndpointWatcher;
import it.unibo.backend.http.JsonUtility;
import it.unibo.backend.mqtt.MQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.mqtt.MQTTTopic;
import it.unibo.backend.serial.SerialCommChannel;
import it.unibo.backend.serial.SerialMessageObserver;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.SystemState;
import it.unibo.backend.temperature.TemperatureSampler;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver {

    private final TemperatureSampler temperatureSampler;
    private double frequency;

    private OperationMode operatingMode;
    private double windowLevel;
    private SystemState currentState;
    private boolean interventionRequired;

    private final HttpClient httpClient;
    private final SerialCommChannel commChannel;
    private final MQTTClient mqttClient;
    private final HttpEndpointWatcher operationClient;
    private final HttpEndpointWatcher alarmClient;

    public ControlUnit(final SerialCommChannel commChannel,
            final MQTTClient mqttClient,
            final HttpClient httpClient,
            final HttpEndpointWatcher operatingModeDaemon,
            final HttpEndpointWatcher interventionDaemon) {

        this.temperatureSampler = new TemperatureSampler();
        this.frequency = 0;
        this.interventionRequired = false;
        this.currentState = new NormalState(this);

        this.httpClient = httpClient;
        this.commChannel = commChannel;
        this.mqttClient = mqttClient;
        this.operationClient = operatingModeDaemon;
        this.alarmClient = interventionDaemon;
    }

    private void setup() {
        this.mqttClient.registerObserver(this);
        this.commChannel.registerObserver(this);
        this.operationClient.registerObserver(this);
        this.alarmClient.registerObserver(this);
        operationClient.start(1000);
        alarmClient.start(1000);
    }

    public void start() throws InterruptedException {
        setup();
        currentState.handle();
        while (true) {
            final SystemState nextState = currentState.next();
            if (nextState != currentState) {
                currentState = nextState;
                currentState.handle();
            }
            sendHttpUpdate();
            sendMqttUpdate();
            sendSerialUpdate();
            Thread.sleep(1000);
        }
    }

    public TemperatureSampler getTemperatureSampler() {
        return this.temperatureSampler;
    }

    public OperationMode getOperatingMode() {
        return this.operatingMode;
    }

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }

    public void setWindowLevel(final int level) {
        this.windowLevel = level;
    }

    public void setNeedForIntervention(final boolean needsIntervention) {
        this.interventionRequired = needsIntervention;
    }

    public boolean needsIntervention() {
        return this.interventionRequired;
    }

    @Override
    public void onMQTTMessageReceived(final String topic, final JsonObject data) {
        if (topic.equals(MQTTTopic.TEMPERATURE.getName())) {
            final double temperature = data.getDouble(JsonUtility.TEMPERATURE);
            this.temperatureSampler.addReading(System.currentTimeMillis(), temperature);
        }
    }

    @Override
    public void onSerialMessageReceived(final JsonObject data) {
        checkAndUpdateOperatingMode(data);
        if (this.operatingMode.equals(OperationMode.MANUAL)) {
            this.windowLevel = data.getDouble(JsonUtility.WINDOW_LEVEL);
        }
    }

    @Override
    public void onHTTPMessageReceived(final JsonObject data) {
        if (data.containsKey(JsonUtility.INTERVENTION_NEED)) {
            this.interventionRequired = data.getBoolean(JsonUtility.INTERVENTION_NEED);
        }
        checkAndUpdateOperatingMode(data);
    }

    private void checkAndUpdateOperatingMode(final JsonObject data) {
        final String operatingMode = data.getString(JsonUtility.OPERATING_MODE);
        if (operatingMode.equals(OperationMode.AUTO.getName())) {
            this.operatingMode = OperationMode.AUTO;
        } else if (operatingMode.equals(OperationMode.MANUAL.getName())) {
            this.operatingMode = OperationMode.MANUAL;
        } else {
            throw new IllegalStateException("Unrecognized operating mode");
        }
    }

    private void sendHttpUpdate() {
        httpClient.sendHttpData("/api/temperature", this.temperatureSampler.getTemperature().asJson());
        httpClient.sendHttpData("/api/report", this.temperatureSampler.getHistory().getLast().asJson());
        JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operatingMode);
        httpClient.sendHttpData("/api/operation", data);
        data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        httpClient.sendHttpData("/api/alarm", data);
        data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.currentState.getName());
        data.put(JsonUtility.SAMPLING_FREQ, this.frequency);
        httpClient.sendHttpData("/api/data", data);
    }

    private void sendMqttUpdate() {
        final JsonObject data = new JsonObject();
        data.put(JsonUtility.SAMPLING_FREQ, this.frequency);
        mqttClient.publish(MQTTTopic.FREQUENCY.getName(), data);
    }

    private void sendSerialUpdate() {
        commChannel.sendMsg(String.format("Level: %.2f|Mode: %s|Temp: $.2f",
            windowLevel,
            operatingMode.getName(),
            temperatureSampler.getTemperature().getValue()));
    }
}
