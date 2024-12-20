#ifndef __IDLE_AUTO__
#define __IDLE_AUTO__

#include "State.h"

class IdleAuto : public State {
    private:
        unsigned long startTime;
    public:
        IdleAuto();
        void handle() override;
        State* next() override;
};

#endif
