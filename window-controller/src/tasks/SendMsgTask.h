#ifndef __SEND_MSG_TASK__
#define __SEND_MSG_TASK__

#include "communication/MsgService.h"
#include "Task.h"

extern MsgServiceClass MsgService;

class SendMsgTask: public Task {
    public:
        void tick() override;
};

#endif
