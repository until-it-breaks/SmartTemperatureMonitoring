#ifndef __WINDOW_OPERATIONAL__
#define __WINDOW_OPERATIONAL__

#include "State.h"

// Represents a composite state in the window operation system.
// This state can transition through various substates such as ActiveAuto, ActiveManual, IdleAuto, and IdleManual.
// It delegates behavior to the current substate and determines the next state based on the system's context.
class WindowOperational : public State {
    public:
        WindowOperational(State* state);
        void handle() override;
        State* next() override;
    private:
        State* currentState;
};

#endif
