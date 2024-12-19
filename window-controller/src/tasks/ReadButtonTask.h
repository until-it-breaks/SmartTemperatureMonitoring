#ifndef __READ_BUTTON__
#define __READ_BUTTON__

#include "Task.h"

class ReadButtonTask: public Task {
    public:
        void tick() override;
};

#endif
