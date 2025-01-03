#include "SendMsgTask.h"
#include "Config.h"

SendMsgTask::SendMsgTask(Context* context) {
    this->context = context;
}

void SendMsgTask::tick() {
    MsgService.sendMsg("Level:" + String(context->getLevel()) + "|Switch:" + String(context->getModeToSwitchTo()));
    context->setModeToSwitchTo(NONE);
}
