package it.unibo.backend.controlUnit;

import it.unibo.backend.mqtt.MQTTAgent;
import it.unibo.backend.temperature.TemperatureSampler;

public class ControlUnit {

    private TemperatureSampler temperatureSampler;
    private MQTTAgent agent;
    private double frequency;

    public ControlUnit(TemperatureSampler temperatureSampler) {
        this.temperatureSampler = temperatureSampler;
        this.agent = new MQTTAgent();
    }

    public void start() {
        while (true) {
            
        }
    }
}
