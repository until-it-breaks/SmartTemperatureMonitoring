#ifndef __READ_POTENTIOMETER_TASK__
#define __READ_POTENTIOMETER_TASK__

#include "Task.h"

class ReadPotentiometerTask: public Task {
    public:
        void tick() override;
};

#endif
