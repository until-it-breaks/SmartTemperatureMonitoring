#ifndef __MAIN_TASK__
#define __MAIN_TASK__

#include "Task.h"
#include "states/State.h"

class MainTask: public Task {
    public:
        MainTask();
        void tick() override;
    private:
        State* currentState;
};

#endif
