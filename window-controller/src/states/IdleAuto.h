#ifndef __IDLE_AUTO__
#define __IDLE_AUTO__

#include "State.h"

class IdleAuto : public State {
    public:
        IdleAuto(Context* context); 
        void handle() override;
        State* next() override;
    private:
        unsigned long startTime;
};

#endif
