#ifndef __WINDOW_CONTROL_TASK__
#define __WINDOW_CONTROL_TASK__

#include "Task.h"
#include "states/State.h"

// Represents the main task responsible for managing the state machine of the window control system.
// This class initializes the state machine, delegates control to the current state, 
// and handles transitions between states during each tick of the system.
class WindowControlTask: public Task {
    public:
        WindowControlTask();
        void tick() override;
    private:
        State* currentState;
};

#endif
