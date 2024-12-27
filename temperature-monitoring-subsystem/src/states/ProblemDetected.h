#ifndef __PROBLEMDETECTED__
#define __PROBLEMDETECTED__

#include "State.h"
#include "../controllers/LedController.h"

extern LedController* ledController;
extern bool isNetworkConnected;

class ProblemDetected : public State {
    public:
        ProblemDetected();
        void execute() override;
        State* next() override;
};

#endif