#include "SendMsgTask.h"

SendMsgTask::SendMsgTask(Context* context) {
    this->context = context;
}

void SendMsgTask::tick() {
    String mode;
    if (context->getOperatingMode() == OperatingMode::AUTO) {
        mode = "auto";
    } else if (context->getOperatingMode() == OperatingMode::MANUAL) {
        mode = "manual";
    }
    MsgService.sendMsg("Level: " + String(context->getLevel()) + "|Mode: " + mode);
}
