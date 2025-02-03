package it.unibo.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.controller.ControlUnit;
import it.unibo.backend.enums.Topic;
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
            final MQTTClient mqttClient = new MQTTClient(Connectivity.MQTT_BROKER_HOST, Connectivity.MQTT_BROKER_PORT);
            final HttpClient httpClient = new HttpClientImpl(Connectivity.SERVER_HOST_LOCAL, Connectivity.SERVER_PORT);
            final ControlUnit controlUnit = new ControlUnit(serialCommChannel, mqttClient, httpClient);
            // Those daemons will be fetching requests from the dashboard.
            final HttpEndpointWatcher modeSwitchRequestsWatcher = new HttpEndpointWatcher(Connectivity.SERVER_HOST_LOCAL, Connectivity.SERVER_PORT, Connectivity.SWITCH_MODE_PATH);
            final HttpEndpointWatcher alarmSwitchRequestWatcher = new HttpEndpointWatcher(Connectivity.SERVER_HOST_LOCAL, Connectivity.SERVER_PORT, Connectivity.SWITCH_ALARM_PATH);

            modeSwitchRequestsWatcher.registerObserver(controlUnit);
            alarmSwitchRequestWatcher.registerObserver(controlUnit);
            serialCommChannel.registerObserver(controlUnit);
            mqttClient.registerObserver(controlUnit);
            modeSwitchRequestsWatcher.start(1000);
            alarmSwitchRequestWatcher.start(1000);
            mqttClient.start();

            logger.info("Starting Control Unit in 5 seconds"); // Wait for the mqtt client to fully estabilish a connection.
            Thread.sleep(5000);
            mqttClient.subscribe(Topic.TEMPERATURE.getName());

            Thread controlUnitThread = new Thread(controlUnit, "ControlUnit");
            controlUnitThread.start();

            // #########################################################################################
            // For testing purposes, comment when an actual temperature sensor is providing data via MQTT
            //Thread testThread = new Thread(new TemperatureTest(controlUnit, 2000), "TemperatureTest");
            //testThread.start();
            // #########################################################################################
        } catch (final Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
        }
    }

    private static SerialCommChannel initSerialChannel(final String[] args) throws SerialPortException {
        final String port = args.length > 0 ? args[0] : Connectivity.DEFAULT_SERIAL_PORT;
        return new SerialCommChannel(port, SerialPort.BAUDRATE_9600);
    }
}