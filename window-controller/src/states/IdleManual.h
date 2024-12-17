#ifndef __IDLE_MANUAL__
#define __IDLE_MANUAL__

#include "State.h"

class IdleManual : public State {
    private:
        unsigned long startTime;
    public:
        IdleManual(State* state);
        void handle() override;
        State* next() override;
};

#endif
