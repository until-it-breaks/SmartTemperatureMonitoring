#ifndef __RECEIVE_MSG__
#define __RECEIVE_MSG__

#include "communication/MsgService.h"
#include "Task.h"
#include "Context.h"

extern MsgServiceClass MsgService;

class ReceiveMsgTask: public Task {
    public:
        ReceiveMsgTask(Context* context);
        void tick() override;
    private:
        Context* context;

};

#endif
