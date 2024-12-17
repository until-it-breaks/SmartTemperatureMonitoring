#ifndef __WINDOW_ALARM__
#define __WINDOW_ALARM__

#include "State.h"

class WindowAlarm : public State {
    public:
        WindowAlarm(State* state);
        void handle() override;
        State* next() override;
    private:
        State* prevState;
};

#endif
