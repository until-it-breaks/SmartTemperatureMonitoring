package it.unibo.backend.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.JsonUtility;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialCommChannel implements SerialPortEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SerialCommChannel.class);
    // Pattern that matches a message like: Level:0.9|Mode:1
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("Level:(\\d+\\.\\d+)|Switch:(\\d+)");

    private final SerialPort serialPort;
    private final StringBuffer currentMsg = new StringBuffer("");
    private String lastMessage;

    private final List<SerialMessageObserver> observers = new ArrayList<>();

    public SerialCommChannel(final String port, final int rate) throws SerialPortException {
        this.serialPort = new SerialPort(port);
        this.serialPort.openPort();
        this.serialPort.setParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        this.serialPort.addEventListener(this);
    }

    public void sendMsg(final String msg) {
        final char[] array = (msg + "\n").toCharArray();
        final byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = (byte) array[i];
        }
        try {
            synchronized (serialPort) {
                serialPort.writeBytes(bytes);
            }
        } catch (final SerialPortException e) {
            logger.error("Failed to send message over serial: {}. {}", e.getMessage(), e.getStackTrace().toString());
        }
    }

    @Override
    public void serialEvent(final SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) {
            try {
                final String msg = serialPort.readString(serialPortEvent.getEventValue());
                currentMsg.append(msg);
                int index;
                while ((index = currentMsg.indexOf("\n")) >= 0) {
                    final String completeMessage = currentMsg.substring(0, index);
                    processMessage(completeMessage);
                    currentMsg.delete(0, index + 1);
                }
            } catch (final SerialPortException e) {
                logger.error("Failed to receive message over serial: {}. {}", e.getMessage(), e.getStackTrace().toString());
            }
        }
    }

    public void close() {
        try {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.closePort();
            }
        } catch (final SerialPortException e) {
            logger.error("Error occured while trying to close serial communication channel", e.getMessage());
        }
    }

    private void processMessage(final String message) {
        if (!message.equals(lastMessage)) {
            final Matcher matcher = MESSAGE_PATTERN.matcher(message);
            boolean containsWindowLevel = false;
            boolean containsOperationMode = false;
            String windowLevel = null;
            String modeToSwitchTo = null;

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    containsWindowLevel = true;
                    windowLevel = matcher.group(1);
                }
                if (matcher.group(2) != null) {
                    containsOperationMode = true;
                    modeToSwitchTo = matcher.group(2);
                }
            }

            if (containsWindowLevel && containsOperationMode) {
                final JsonObject jsonMessage = new JsonObject()
                    .put(JsonUtility.WINDOW_LEVEL, Double.parseDouble(windowLevel))
                    .put(JsonUtility.REQUESTED_MODE, Integer.parseInt(modeToSwitchTo));
                notifyObservers(jsonMessage);
            }
            lastMessage = message;
        }
    }

    public void registerObserver(final SerialMessageObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(final SerialMessageObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(final JsonObject message) {
        for (final SerialMessageObserver observer : observers) {
            observer.onSerialMessageReceived(message);
        }
    }
}
