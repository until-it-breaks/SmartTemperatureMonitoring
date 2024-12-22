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

    public static void main(final String[] args) {
        try {
            final SerialCommChannel serialCommChannel = initSerialChannel(args);
            final MQTTClient mqttClient = new MQTTClient(ConnectivityConfig.MQTT_BROKER_HOST, ConnectivityConfig.MQTT_BROKER_PORT);
            final HttpClient httpClient = new HttpClientImpl(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT);

            final ControlUnit controlUnit = new ControlUnit(serialCommChannel, mqttClient, httpClient);

            final HttpEndpointWatcher operationModeWatcher = initWatcher(ConnectivityConfig.OPERATING_MODE_PATH, controlUnit);
            final HttpEndpointWatcher interventionRequirementWatcher = initWatcher(ConnectivityConfig.INTERVENTION_PATH, controlUnit);

            mqttClient.start();
            logger.info("Starting Control Unit in 5 seconds");
            Thread.sleep(5000);
            controlUnit.start();
            addShutdownHook(serialCommChannel, mqttClient, operationModeWatcher, interventionRequirementWatcher);

        } catch (final Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
        }
    }

    private static SerialCommChannel initSerialChannel(final String[] args) throws SerialPortException {
        final String port = args.length > 0 ? args[0] : ConnectivityConfig.DEFAULT_SERIAL_PORT;
        return new SerialCommChannel(port, SerialPort.BAUDRATE_9600);
    }

    private static HttpEndpointWatcher initWatcher(final String path, final ControlUnit controlUnit) {
        final HttpEndpointWatcher watcher = new HttpEndpointWatcher(ConnectivityConfig.SERVER_HOST_LOCAL, ConnectivityConfig.SERVER_PORT, path);
        watcher.registerObserver(controlUnit);
        watcher.start(1000);
        return watcher;
    }

    private static void addShutdownHook(final SerialCommChannel serialCommChannel, final MQTTClient mqttClient,
                                        final HttpEndpointWatcher... watchers) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serialCommChannel.close();
                mqttClient.stop();
                for (final HttpEndpointWatcher watcher : watchers) {
                    watcher.stop();
                }
            } catch (final Exception e) {
                logger.error("Error during shutdown: {}", e.getMessage(), e);
            }
        }));
    }
}