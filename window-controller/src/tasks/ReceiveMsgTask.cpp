#include "ReceiveMsgTask.h"
#include <Arduino.h>

extern String stateMessage;

void ReceiveMsgTask::tick() {
    if (MsgService.isMsgAvailable()) {
        Msg* msg = MsgService.receiveMsg();
        if (msg->getContent() == "1") {
            // Do something
        } else if (msg->getContent() == "0") {
            // Do something else
        } 
        delete msg;
    }
}