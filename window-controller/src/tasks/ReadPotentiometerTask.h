#ifndef __READ_POTENTIOMETER__
#define __READ_POTENTIOMETER__

#include "Task.h"

class ReadPotentiometerTask: public Task {
    public:
        void tick() override;
};

#endif
