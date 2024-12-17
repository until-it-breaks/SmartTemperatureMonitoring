#ifndef __WINDOW_OPERATIONAL__
#define __WINDOW_OPERATIONAL__

#include "State.h"

class WindowOperational : public State {
    public:
        WindowOperational(State* state);
        void handle() override;
        State* next() override;
    private:
        State* currentState;
};

#endif
