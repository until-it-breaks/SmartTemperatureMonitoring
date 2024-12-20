#ifndef __ACTIVE_AUTO__
#define __ACTIVE_AUTO__

#include "State.h"

class ActiveAuto : public State {
    public:
        void handle() override;
        State* next() override;
};

#endif
