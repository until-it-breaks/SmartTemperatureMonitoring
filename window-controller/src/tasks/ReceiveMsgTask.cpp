#include "ReceiveMsgTask.h"
#include "Arduino.h"
#include "Config.h"

ReceiveMsgTask::ReceiveMsgTask(Context* context) {
    this->context = context;
}

void ReceiveMsgTask::tick() {
    if (MsgService.isMsgAvailable()) {
        Msg* msg = MsgService.receiveMsg();
        String content = msg->getContent();
        
        if (content.startsWith("Level:")) {
            // Parse the content based on the expected format (AUTO is 1, MANUAL is 2)
            // Example: "Level:23.45|Mode:1|Temp:21.5|Alarm:1"
            int levelStart = content.indexOf("Level:") + 6;
            int levelEnd = content.indexOf("|", levelStart);
            String levelStr = content.substring(levelStart, levelEnd);
            context->setLevel(levelStr.toFloat());
            
            int modeStart = content.indexOf("Mode:") + 5;
            int modeEnd = content.indexOf("|", modeStart);
            String mode = content.substring(modeStart, modeEnd);
            int modeInt = mode.toInt();

            if (modeInt == AUTO) {
                context->setOperatingMode(AUTO);
            } else if (modeInt == MANUAL) {
                context->setOperatingMode(MANUAL);
            }

            int tempStart = content.indexOf("Temp:") + 5;
            int tempEnd = content.indexOf("|", tempStart);
            String tempStr = content.substring(tempStart, tempEnd);
            context->setTemperature(tempStr.toFloat());

            int alarmStart = content.indexOf("Alarm:") + 6;
            String alarmStr = content.substring(alarmStart);
            if (alarmStr.toInt() == 1) {
                context->turnOnAlarmMode();
            } else if (alarmStr.toInt() == 0) {
                context->turnOffAlarmMode();
            }
        }
        delete msg;
    }
}
