#ifndef __IDLE__
#define __IDLE__

#include "State.h"
#include "../controllers/LedController.h"

extern LedController* ledController;

class Idle : public State {
    public:
        Idle();
        void init() override;
        State* handle() override;
};

#endif