#include "ReceiveMsgTask.h"
#include <Arduino.h>

ReceiveMsgTask::ReceiveMsgTask(Context* context) {
    this->context = context;
}

void ReceiveMsgTask::tick() {
    if (MsgService.isMsgAvailable()) {
        Msg* msg = MsgService.receiveMsg();
        String content = msg->getContent();
        
        if (content.startsWith("Level:") && content.indexOf("|") != -1) {
            // Parse the content based on the expected format
            // Example: "Level: 23.45|Mode: AUTO|Temp: 21.5"
            int levelStart = content.indexOf("Level:") + 7;
            int levelEnd = content.indexOf("|", levelStart);
            String levelStr = content.substring(levelStart, levelEnd);
            context->setLevel(levelStr.toFloat());
            
            int modeStart = content.indexOf("Mode:") + 5;
            int modeEnd = content.indexOf("|", modeStart);
            String mode = content.substring(modeStart, modeEnd);
            
            if (mode == "auto" && context->getOperatingMode() == OperatingMode::MANUAL) {
                context->setOperatingMode(OperatingMode::AUTO);
            } else if (mode == "manual" && context->getOperatingMode() == OperatingMode::AUTO) {
                context->setOperatingMode(OperatingMode::MANUAL);
            }

            int tempStart = content.indexOf("Temp:") + 5;
            String tempStr = content.substring(tempStart);
            context->setTemperature(tempStr.toFloat());
        }
        delete msg;
    }
}

//TODO still missin alarm.