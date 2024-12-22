package it.unibo.backend.controlunit;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.enums.Topic;
import it.unibo.backend.http.client.HttpClient;
import it.unibo.backend.http.client.HttpEndpointObserver;
import it.unibo.backend.http.client.HttpEndpointWatcher;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.mqtt.MQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.serial.SerialCommChannel;
import it.unibo.backend.serial.SerialMessageObserver;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.State;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;
import it.unibo.backend.temperature.TemperatureSampler;
import it.unibo.backend.ConnectivityConfig;
import it.unibo.backend.JsonUtility;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver {

    private final TemperatureSampler sampler;
    private double frequency;

    private OperatingMode operatingMode;
    private double windowLevel;
    private boolean interventionRequired;
    private State currentState;

    private final HttpClient httpClient;
    private final SerialCommChannel commChannel;
    private final MQTTClient mqttClient;
    private final HttpEndpointWatcher operationClient;
    private final HttpEndpointWatcher alarmClient;

    private TemperatureSample lastSample;
    private TemperatureReport lastReport;
    private OperatingMode lastMode;
    private boolean lastInterventionUpdate;
    private double lastWindowLevel;
    private double lastHttpFrequency;
    private double lastMqttFrequency;
    private SystemState lastStateAlias;
    private String lastSerialMessage;

    public ControlUnit(final SerialCommChannel commChannel,
            final MQTTClient mqttClient,
            final HttpClient httpClient,
            final HttpEndpointWatcher operatingModeDaemon,
            final HttpEndpointWatcher interventionDaemon) {

        this.sampler = new TemperatureSampler();
        this.frequency = 1.0;
        this.operatingMode = OperatingMode.AUTO;
        this.windowLevel = 0.0;
        this.interventionRequired = false;
        this.currentState = new NormalState(this);

        this.httpClient = httpClient;
        this.commChannel = commChannel;
        this.mqttClient = mqttClient;
        this.operationClient = operatingModeDaemon;
        this.alarmClient = interventionDaemon;
    }

    private void setup() throws InterruptedException {
        this.mqttClient.registerObserver(this);
        this.commChannel.registerObserver(this);
        this.operationClient.registerObserver(this);
        this.alarmClient.registerObserver(this);
        Thread.sleep(5000);
    }

    public void start() throws InterruptedException {
        setup();
        currentState.handle();
        Thread.sleep(1000);
        while (true) {
            processState();
            sendHttpUpdate();
            sendMqttUpdate();
            sendSerialUpdate();
            Thread.sleep(1000);
        }
    }

    private void processState() {
        final State nextState = currentState.next();
        if (nextState != currentState) {
            currentState = nextState;
            currentState.handle();
        }
    }

    public TemperatureSampler getSampler() {
        return this.sampler;
    }

    public OperatingMode getOperatingMode() {
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
        if (topic.equals(Topic.TEMPERATURE.getName())) {
            final double temperature = data.getDouble(JsonUtility.TEMPERATURE);
            this.sampler.addReading(System.currentTimeMillis(), temperature);
        }
    }

    @Override
    public void onSerialMessageReceived(final JsonObject data) {
        final int mode = data.getInteger(JsonUtility.OPERATING_MODE);
        if (mode == 0) {
            this.operatingMode = OperatingMode.AUTO;
        } else if (mode == 1) {
            this.operatingMode = OperatingMode.MANUAL;
        }
        if (this.operatingMode.equals(OperatingMode.MANUAL)) {
            this.windowLevel = data.getDouble(JsonUtility.WINDOW_LEVEL);
        }
    }

    @Override
    public void onHTTPMessageReceived(final JsonObject data) {
        if (data.containsKey(JsonUtility.INTERVENTION_NEED)) {
            this.interventionRequired = data.getBoolean(JsonUtility.INTERVENTION_NEED);
        }
        if (data.containsKey(JsonUtility.OPERATING_MODE)) {
            final String mode = data.getString(JsonUtility.OPERATING_MODE);
            if (mode.equals(OperatingMode.AUTO.getName())) {
                this.operatingMode = OperatingMode.AUTO;
            } else if (mode.equals(OperatingMode.MANUAL.getName())) {
                this.operatingMode = OperatingMode.MANUAL;
            }
        }
    }

    private void sendHttpUpdate() {
        final TemperatureSample sample = sampler.getTemperature();
        if (sample != null && !sample.equals(lastSample)) {
            httpClient.sendHttpData(ConnectivityConfig.TEMPERATURE_PATH, sample.asJson());
            lastSample = sample;
        }
        if (!sampler.getHistory().isEmpty()) {
            final TemperatureReport report = sampler.getHistory().getLast();
            if (!report.equals(lastReport)) {
                httpClient.sendHttpData(ConnectivityConfig.REPORTS_PATH, this.sampler.getHistory().getLast().asJson());
                lastReport = report;
            }
        }
        JsonObject data = new JsonObject();

        final OperatingMode mode = this.operatingMode;
        if (!mode.equals(lastMode)) {
            data.put(JsonUtility.OPERATING_MODE, mode.getName());
            httpClient.sendHttpData(ConnectivityConfig.OPERATING_MODE_PATH, data);
            lastMode = mode;
            data = new JsonObject();
        }

        final boolean needsIntervention = this.interventionRequired;
        if (needsIntervention != lastInterventionUpdate) {
            data.put(JsonUtility.INTERVENTION_NEED, needsIntervention);
            httpClient.sendHttpData(ConnectivityConfig.INTERVENTION_PATH, data);
            data = new JsonObject();
            lastInterventionUpdate = needsIntervention;
        }

        final double windowLevel = this.windowLevel;
        final SystemState state = this.currentState.getStateAlias();
        final double frequency = this.frequency;

        if (windowLevel != lastWindowLevel || !state.equals(lastStateAlias) || frequency != lastHttpFrequency) {
            data.put(JsonUtility.WINDOW_LEVEL, windowLevel);
            data.put(JsonUtility.SYSTEM_STATE, state.getName());
            data.put(JsonUtility.FREQ_MULTIPLIER, frequency);
            httpClient.sendHttpData(ConnectivityConfig.CONFIG_PATH, data);
            lastWindowLevel = windowLevel;
            lastStateAlias = state;
            lastHttpFrequency = frequency;
        }
    }

    private void sendMqttUpdate() {
        if (this.frequency != lastMqttFrequency) {
            final JsonObject data = new JsonObject();
            data.put(JsonUtility.FREQ_MULTIPLIER, this.frequency);
            mqttClient.publish(Topic.FREQUENCY.getName(), data);
            lastMqttFrequency = frequency;
        }
    }

    private void sendSerialUpdate() {
        final TemperatureSample sample = sampler.getTemperature();
        if (sample != null) {
            final String message = String.format("Level:%.2f|Mode:%d|Temp:$.2f|Alarm:%d",
            windowLevel,
            operatingMode.getValue(),
            sample.getValue(),
            this.interventionRequired ? 1 : 0);

            if (!message.equals(lastSerialMessage)) {
                commChannel.sendMsg(message);
                lastSerialMessage = message;
            }
        }
    }
}
