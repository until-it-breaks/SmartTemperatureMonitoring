package it.unibo.backend.controlUnit;

import it.unibo.backend.mqtt.BackendMQTTClient;
import it.unibo.backend.serial.CommChannel;
import it.unibo.backend.states.OperationMode;
import it.unibo.backend.temperature.TemperatureSampler;

public class ControlUnit {

    private TemperatureSampler temperatureSampler;
    private double frequency;
    private CommChannel commChannel;
    private BackendMQTTClient agent;
    private OperationMode operationMode;
    private double windowLevel;

    public ControlUnit(TemperatureSampler temperatureSampler, CommChannel commChannel) {
        this.temperatureSampler = temperatureSampler;
        this.commChannel = commChannel;
    }

    public void start() {
        while (true) {
            commChannel.sendMsg(String.format("Level: %.2f|Mode: %s|Temp: $.2f", windowLevel, operationMode, temperatureSampler.getTemperature()));
        }
    }
}
