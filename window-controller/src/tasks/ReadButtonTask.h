#ifndef __READ_BUTTON_TASK__
#define __READ_BUTTON_TASK__

#include "Task.h"
#include "components/Button.h"
#include "Context.h"

class ReadButtonTask: public Task {
    public:
        ReadButtonTask(Context* context);
        void tick() override;
    private:
        Button* button;
        Context* context;
};

#endif
