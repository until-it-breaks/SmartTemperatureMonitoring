#ifndef __READ_BUTTON_TASK__
#define __READ_BUTTON_TASK__

#include "Task.h"
#include "components/Button.h"

class ReadButtonTask: public Task {
    public:
        ReadButtonTask();
        void tick() override;
    private:
        Button* button;
};

#endif
