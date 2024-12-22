package it.unibo.backend.controlunit;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.enums.Topic;
import it.unibo.backend.http.client.HttpClient;
import it.unibo.backend.http.client.HttpEndpointObserver;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.mqtt.MQTTClient;
import it.unibo.backend.mqtt.MQTTMessageObserver;
import it.unibo.backend.serial.SerialCommChannel;
import it.unibo.backend.serial.SerialMessageObserver;
import it.unibo.backend.states.NormalState;
import it.unibo.backend.states.State;
import it.unibo.backend.temperature.TemperatureSampler;
import it.unibo.backend.JsonUtility;
import it.unibo.backend.controlunit.managers.HttpUpdateManager;
import it.unibo.backend.controlunit.managers.MqttUpdateManager;
import it.unibo.backend.controlunit.managers.SerialUpdateManager;
import it.unibo.backend.controlunit.managers.UpdateManager;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver {

    private final TemperatureSampler sampler;
    private final List<UpdateManager> updateManagers;
    private double frequency;

    private OperatingMode operatingMode;
    private double windowLevel;
    private boolean interventionRequired;
    private State currentState;

    public ControlUnit(final SerialCommChannel commChannel,
            final MQTTClient mqttClient,
            final HttpClient httpClient) {

        this.sampler = new TemperatureSampler();
        this.updateManagers = new ArrayList<>();
        this.updateManagers.add(new HttpUpdateManager(httpClient));
        this.updateManagers.add(new MqttUpdateManager(mqttClient));
        this.updateManagers.add(new SerialUpdateManager(commChannel));

        this.frequency = 1.0;
        this.operatingMode = OperatingMode.AUTO;
        this.windowLevel = 0.0;
        this.interventionRequired = false;
        this.currentState = new NormalState(this);
    }

    public void start() throws InterruptedException {
        currentState.handle();
        Thread.sleep(1000);
        while (true) {
            processState();
            sendUpdates();
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

    private void sendUpdates() {
        for (final UpdateManager updateManager : updateManagers) {
            updateManager.sendUpdate(new ControlUnitData(frequency, 
            operatingMode, 
            windowLevel, 
            interventionRequired, 
            this.currentState.getStateAlias(), 
            this.sampler.getSample(), 
            this.sampler.getLastReport()));
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
}
