#ifndef __IDLE__
#define __IDLE__

#include "State.h"
#include "../controllers/LedController.h"

extern LedController* ledController;
extern bool isNetworkConnected;


class Idle : public State {
    public:
        Idle();
        void execute() override;
        State* next() override;
};

#endif