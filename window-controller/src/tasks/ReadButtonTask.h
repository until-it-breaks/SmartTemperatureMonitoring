#ifndef __READ_BUTTON_TASK__
#define __READ_BUTTON__

#include "Task.h"

class ReadButtonTask: public Task {
    public:
        void tick() override;
};

#endif
