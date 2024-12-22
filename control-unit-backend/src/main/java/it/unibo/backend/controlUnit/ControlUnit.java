package it.unibo.backend.controlunit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.WindowLevel;
import it.unibo.backend.controlunit.managers.HttpUpdateManager;
import it.unibo.backend.controlunit.managers.MqttUpdateManager;
import it.unibo.backend.controlunit.managers.SerialUpdateManager;
import it.unibo.backend.controlunit.managers.UpdateManager;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver {

    private final TemperatureSampler sampler;
    private final List<UpdateManager> updateManagers;

    private double freqMultiplier;
    private AtomicReference<Double> windowLevel;
    private AtomicBoolean needsIntervention;
    private AtomicReference<OperatingMode> mode;
    private State currentState;

    public ControlUnit(final SerialCommChannel commChannel,
            final MQTTClient mqttClient,
            final HttpClient httpClient) {

        this.sampler = new TemperatureSampler();
        this.updateManagers = new ArrayList<>();
        this.updateManagers.add(new HttpUpdateManager(httpClient));
        this.updateManagers.add(new MqttUpdateManager(mqttClient));
        this.updateManagers.add(new SerialUpdateManager(commChannel));

        this.freqMultiplier = FreqMultiplier.NORMAL;
        this.mode = new AtomicReference<>(OperatingMode.AUTO);
        this.windowLevel = new AtomicReference<>(WindowLevel.FULLY_CLOSED);
        this.needsIntervention = new AtomicBoolean(false);
        this.currentState = new NormalState(this);
    }

    public void start() throws InterruptedException {
        while (true) {
            processState();
            sendUpdates();
            Thread.sleep(1000);
        }
    }

    public TemperatureSampler getSampler() {
        return this.sampler;
    }

    public OperatingMode getMode() {
        return this.mode.get();
    }

    public void setFreqMultiplier(final double frequency) {
        this.freqMultiplier = frequency;
    }

    public void setWindowLevel(final double level) {
        this.windowLevel.set(level);
    }

    public void setNeedForIntervention(final boolean needsIntervention) {
        this.needsIntervention.set(needsIntervention);
    }

    public boolean needsIntervention() {
        return this.needsIntervention.get();
    }

    @Override
    public void onMQTTMessageReceived(final String topic, final JsonObject data) {
        if (topic.equals(Topic.TEMPERATURE.getName())) {
            final double temperature = data.getDouble(JsonUtility.TEMPERATURE);
            this.sampler.addSample(System.currentTimeMillis(), temperature);
        }
    }

    @Override
    public void onSerialMessageReceived(final JsonObject data) {
        final int mode = data.getInteger(JsonUtility.OPERATING_MODE);
        if (mode == 0) {
            this.mode.set(OperatingMode.AUTO);
        } else if (mode == 1) {
            this.mode.set(OperatingMode.MANUAL);
        }
        if (this.mode.get().equals(OperatingMode.MANUAL)) {
            this.windowLevel.set(data.getDouble(JsonUtility.WINDOW_LEVEL));
        }
    }

    @Override
    public void onHTTPMessageReceived(final JsonObject data) {
        if (data.containsKey(JsonUtility.INTERVENTION_NEED)) {
            this.needsIntervention.set(data.getBoolean(JsonUtility.INTERVENTION_NEED));
        }
        if (data.containsKey(JsonUtility.OPERATING_MODE)) {
            final String mode = data.getString(JsonUtility.OPERATING_MODE);
            if (mode.equals(OperatingMode.AUTO.getName())) {
                this.mode.set(OperatingMode.AUTO);
            } else if (mode.equals(OperatingMode.MANUAL.getName())) {
                this.mode.set(OperatingMode.MANUAL);
            }
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
            updateManager.sendUpdate(new ControlUnitData(this.freqMultiplier, 
                this.mode.get(), 
                this.windowLevel.get(), 
                this.needsIntervention.get(), 
                this.currentState.getStateAlias(), 
                this.sampler.getLastSample(), 
                this.sampler.getLastReport()));
        }
    }
}
