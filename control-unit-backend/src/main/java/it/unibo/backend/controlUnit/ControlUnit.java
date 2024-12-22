package it.unibo.backend.controlunit;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.enums.MQTTTopic;
import it.unibo.backend.enums.OperationMode;
import it.unibo.backend.http.HttpClient;
import it.unibo.backend.http.HttpEndpointObserver;
import it.unibo.backend.http.HttpEndpointWatcher;
import it.unibo.backend.mqtt.MQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.serial.SerialCommChannel;
import it.unibo.backend.serial.SerialMessageObserver;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.SystemState;
import it.unibo.backend.temperature.TemperatureSample;
import it.unibo.backend.temperature.TemperatureSampler;
import it.unibo.backend.ConnectivityConfig;
import it.unibo.backend.JsonUtility;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver {

    private final TemperatureSampler temperatureSampler;
    private double frequency;

    private OperationMode operatingMode;
    private double windowLevel;
    private boolean interventionRequired;
    private SystemState currentState;

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
        this.operatingMode = OperationMode.AUTO;
        this.windowLevel = 0.0;
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException {
        setup();
        currentState.handle();
        Thread.sleep(1000);
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
        final int mode = data.getInteger(JsonUtility.OPERATING_MODE);
        if (mode == 0) {
            this.operatingMode = OperationMode.AUTO;
        } else if (mode == 1) {
            this.operatingMode = OperationMode.MANUAL;
        }
        if (this.operatingMode.equals(OperationMode.MANUAL)) {
            this.windowLevel = data.getDouble(JsonUtility.WINDOW_LEVEL);
        }
    }

    @Override
    public synchronized void onHTTPMessageReceived(final JsonObject data) {
        System.err.println("about to send");
        this.interventionRequired = data.getBoolean(JsonUtility.INTERVENTION_NEED);
        final String mode = data.getString(JsonUtility.OPERATING_MODE);
        if (mode.equals(OperationMode.AUTO.getName())) {
            this.operatingMode = OperationMode.AUTO;
        } else if (mode.equals(OperationMode.MANUAL.getName())) {
            this.operatingMode = OperationMode.MANUAL;
        }
    }

    private void sendHttpUpdate() {
        if (temperatureSampler.getTemperature() != null) {
            httpClient.sendHttpData(ConnectivityConfig.TEMPERATURE_PATH, this.temperatureSampler.getTemperature().asJson());
        }
        if (!temperatureSampler.getHistory().isEmpty()) {
            httpClient.sendHttpData(ConnectivityConfig.REPORTS_PATH, this.temperatureSampler.getHistory().getLast().asJson());
        }
        JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operatingMode.getName());
        httpClient.sendHttpData(ConnectivityConfig.OPERATING_MODE_PATH, data);
        data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        httpClient.sendHttpData(ConnectivityConfig.INTERVENTION_PATH, data);
        data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.currentState.getName());
        data.put(JsonUtility.FREQ_MULTIPLIER, this.frequency);
        httpClient.sendHttpData(ConnectivityConfig.CONFIG_PATH, data);
    }

    private void sendMqttUpdate() {
        final JsonObject data = new JsonObject();
        data.put(JsonUtility.FREQ_MULTIPLIER, this.frequency);
        mqttClient.publish(MQTTTopic.FREQUENCY.getName(), data);
    }

    private void sendSerialUpdate() {
        final TemperatureSample sample = temperatureSampler.getTemperature();
        if (sample != null) {
            commChannel.sendMsg(String.format("Level:%.2f|Mode:%d|Temp:$.2f|Alarm:%d",
                windowLevel,
                operatingMode.getValue(),
                sample.getValue(),
                this.interventionRequired ? 1 : 0));
        }
    }
}
