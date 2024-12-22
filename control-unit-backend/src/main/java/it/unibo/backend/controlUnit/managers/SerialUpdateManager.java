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
        /**
         * Test portion
        final String test = String.format("Level:%.2f|Mode:%d|Temp:%.2f|Alarm:%d",
            0.9,
            data.getMode().getValue(),
            23.5,
            data.isInterventionRequired() ? 1 : 0);
        commChannel.sendMsg(test);
        lastSerialMessage = test;
        */
        if (data.getSample() != null) {
            final String message = String.format("Level:%.2f|Mode:%d|Temp:$.2f|Alarm:%d",
            data.getWindowLevel(),
            data.getMode().getValue(),
            data.getSample().getValue(),
            data.isInterventionRequired() ? 1 : 0);
            if (!message.equals(lastSerialMessage)) {
                commChannel.sendMsg(message);
                lastSerialMessage = message;
            }
        }
    }
}
