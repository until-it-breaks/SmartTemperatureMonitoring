#ifndef __IDLE_MANUAL__
#define __IDLE_MANUAL__

#include "State.h"

class IdleManual : public State {
    public:
        IdleManual(Context* context);
        void handle() override;
        State* next() override;
    private:
        unsigned long startTime;
};

#endif
