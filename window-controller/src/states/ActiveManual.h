#ifndef __ACTIVE_MANUAL__
#define __ACTIVE_MANUAL__

#include "State.h"

class ActiveManual : public State {
    public:
        void handle() override;
        State* next() override;
};

#endif
