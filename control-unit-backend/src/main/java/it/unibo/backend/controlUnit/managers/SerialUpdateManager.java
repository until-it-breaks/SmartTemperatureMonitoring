package it.unibo.backend.controlunit.managers;

import it.unibo.backend.controlunit.ControlUnitData;
import it.unibo.backend.serial.SerialCommChannel;

public class SerialUpdateManager implements UpdateManager {
    private final SerialCommChannel commChannel;
    private String lastSerialMessage;

    public SerialUpdateManager(final SerialCommChannel commChannel) {
        this.commChannel = commChannel;
    }

    @Override
    public void sendUpdate(final ControlUnitData data) {
        if (data.getSample() != null) {
            final String message = String.format("Level:%.2f|Mode:%d|Temp:%.2f|Alarm:%d",
            data.getWindowLevel(),
            data.getMode().getValue(),
            data.getSample().getTemperature(),
            data.isInterventionRequired() ? 1 : 0);
            if (!message.equals(lastSerialMessage)) {
                commChannel.sendMsg(message);
                lastSerialMessage = message;
            }
        }
    }
}
