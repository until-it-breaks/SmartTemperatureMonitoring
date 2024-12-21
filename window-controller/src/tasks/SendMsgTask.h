#ifndef __SEND_MSG_TASK__
#define __SEND_MSG_TASK__

#include "communication/MsgService.h"
#include "Task.h"
#include "Context.h"

extern MsgServiceClass MsgService;

class SendMsgTask: public Task {
    public:
        SendMsgTask(Context* context);
        void tick() override;
    private:
        Context* context;
};

#endif
