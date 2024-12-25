package it.unibo.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.WindowLevel;
import it.unibo.backend.controller.handlers.HttpMessageHandler;
import it.unibo.backend.controller.handlers.MessageHandler;
import it.unibo.backend.controller.handlers.MqttMessageHandler;
import it.unibo.backend.controller.handlers.SerialMessageHandler;
import it.unibo.backend.controller.managers.HttpUpdateManager;
import it.unibo.backend.controller.managers.MqttUpdateManager;
import it.unibo.backend.controller.managers.SerialUpdateManager;
import it.unibo.backend.controller.managers.UpdateManager;

public class ControlUnit implements MQTTMessageObserver, SerialMessageObserver, HttpEndpointObserver, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ControlUnit.class);

    private final TemperatureSampler sampler;
    private final List<UpdateManager> updateManagers;

    // Since those are modified by the vertx threads atomic was the easiest way to handle shared variables.
    private final AtomicReference<Double> windowLevel;
    private final AtomicBoolean needsIntervention;
    private final AtomicReference<OperatingMode> mode;

    private double freqMultiplier;
    private State currentState;

    private final MessageHandler mqttHandler;
    private final MessageHandler serialHandler;
    private final MessageHandler httpHandler;

    public ControlUnit(final SerialCommChannel commChannel,
            final MQTTClient mqttClient,
            final HttpClient httpClient) {

        this.sampler = new TemperatureSampler();
        this.freqMultiplier = FreqMultiplier.NORMAL;
        this.mode = new AtomicReference<>(OperatingMode.AUTO);
        this.windowLevel = new AtomicReference<>(WindowLevel.FULLY_CLOSED);
        this.needsIntervention = new AtomicBoolean(false);
        this.currentState = new NormalState(this);

        this.updateManagers = new ArrayList<>();
        this.updateManagers.add(new HttpUpdateManager(httpClient));
        this.updateManagers.add(new MqttUpdateManager(mqttClient));
        this.updateManagers.add(new SerialUpdateManager(commChannel));

        this.mqttHandler = new MqttMessageHandler(this.sampler);
        this.serialHandler = new SerialMessageHandler(this);
        this.httpHandler = new HttpMessageHandler(this, httpClient);
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

    public void setMode(final OperatingMode mode) {
        this.mode.set(mode);
    }

    public OperatingMode getMode() {
        return this.mode.get();
    }

    public void setFreqMultiplier(final double frequency) {
        this.freqMultiplier = frequency;
    }

    public double getWindowLevel() {
        return this.windowLevel.get();
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
        logger.info("Received MQTT message of topic [{}]: {}", topic, data);
        if (topic.equals(Topic.TEMPERATURE.getName())) {
            mqttHandler.handleMessage(data);
        }
    }

    @Override
    public void onSerialMessageReceived(final JsonObject data) {
        logger.info("Received serial message: {}", data);
        serialHandler.handleMessage(data);
    }

    @Override
    public void onHTTPMessageReceived(final JsonObject data) {
        logger.info("Received HTTP message: {}", data);
        httpHandler.handleMessage(data);
    }

    private void processState() {
        logger.info("Current state: {}", currentState.getStateAlias());
        final State nextState = currentState.next();
        if (nextState != currentState && nextState != null) {
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

    @Override
    public void run() {
        try {
            this.start();
        } catch (final InterruptedException e) {
            logger.error("Thread interrupted: {}", e.getMessage());
        }
    }
}
