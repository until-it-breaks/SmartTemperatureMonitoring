#ifndef __ACTIVE_MANUAL__
#define __ACTIVE_MANUAL__

#include "State.h"

class ActiveManual : public State {
    public:
        ActiveManual(Context* context): State(context) {};
        void handle() override;
        State* next() override;
};

#endif
