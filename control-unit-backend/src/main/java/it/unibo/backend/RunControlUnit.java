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

    /**
     * Default serial port is COM4. Can be set as argument with {@code ./gradlew run --args="PORTNAME"}
     */
    public static void main(final String[] args) {
        try {
            final SerialCommChannel serialCommChannel = initSerialChannel(args);
            final MQTTClient mqttClient = new MQTTClient(ConnectivityConfig.MQTT_BROKER_HOST, ConnectivityConfig.MQTT_BROKER_PORT);
            final HttpClient httpClient = new HttpClientImpl(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT);
            final ControlUnit controlUnit = new ControlUnit(serialCommChannel, mqttClient, httpClient);
            final HttpEndpointWatcher opWatcher = new HttpEndpointWatcher(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT, ConnectivityConfig.OPERATING_MODE_PATH);
            final HttpEndpointWatcher intWatcher = new HttpEndpointWatcher(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT, ConnectivityConfig.OPERATING_MODE_PATH);
            opWatcher.registerObserver(controlUnit);
            intWatcher.registerObserver(controlUnit);
            opWatcher.start(1000);
            intWatcher.start(1000);
            mqttClient.start();
            logger.info("Starting Control Unit in 5 seconds");
            Thread.sleep(5000);
            controlUnit.start();

        } catch (final Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
        }
    }

    private static SerialCommChannel initSerialChannel(final String[] args) throws SerialPortException {
        final String port = args.length > 0 ? args[0] : ConnectivityConfig.DEFAULT_SERIAL_PORT;
        return new SerialCommChannel(port, SerialPort.BAUDRATE_9600);
    }
}