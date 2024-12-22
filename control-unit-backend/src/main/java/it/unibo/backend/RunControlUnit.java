package it.unibo.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.http.client.HttpClient;
import it.unibo.backend.http.client.HttpClientImpl;
import it.unibo.backend.http.client.HttpEndpointWatcher;
import it.unibo.backend.mqtt.MQTTClient;
import it.unibo.backend.serial.SerialCommChannel;
import jssc.SerialPort;
import jssc.SerialPortException;

public class RunControlUnit {
    private static final Logger logger = LoggerFactory.getLogger(RunControlUnit.class);

    public static void main(String[] args) {
        try {
            HttpEndpointWatcher operationModeWatcher = new HttpEndpointWatcher(ConnectivityConfig.SERVER_HOST_LOCAL,
                ConnectivityConfig.SERVER_PORT, ConnectivityConfig.OPERATING_MODE_PATH);
            HttpEndpointWatcher interventionRequirementWatcher = new HttpEndpointWatcher(ConnectivityConfig.SERVER_HOST_LOCAL,
                ConnectivityConfig.SERVER_PORT, ConnectivityConfig.INTERVENTION_PATH);

            SerialCommChannel serialCommChannel;
            if (args.length != 0) {
                serialCommChannel = new SerialCommChannel(args[0], SerialPort.BAUDRATE_9600);
            } else {
                serialCommChannel = new SerialCommChannel(ConnectivityConfig.DEFAULT_SERIAL_PORT, SerialPort.BAUDRATE_9600);
            }
            MQTTClient mqttClient = new MQTTClient(ConnectivityConfig.MQTT_BROKER_HOST, ConnectivityConfig.MQTT_BROKER_PORT);
            HttpClient httpClient = new HttpClientImpl(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT);

            ControlUnit controlUnit = new ControlUnit(serialCommChannel, mqttClient, httpClient);
            operationModeWatcher.registerObserver(controlUnit);
            interventionRequirementWatcher.registerObserver(controlUnit);
            serialCommChannel.registerObserver(controlUnit);
            mqttClient.registerObserver(controlUnit);

            operationModeWatcher.start(1000);
            interventionRequirementWatcher.start(1000);
            mqttClient.start();
            controlUnit.start();

        } catch (SerialPortException e) {
            logger.error("Failed to initialize serial communication channel: {}", e.getMessage(), e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
