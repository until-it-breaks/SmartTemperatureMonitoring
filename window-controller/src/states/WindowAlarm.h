#ifndef __WINDOW_ALARM__
#define __WINDOW_ALARM__

#include "State.h"

// Represents the alarm state in the window operation system.
// This state is entered when the system detects an alarm condition.
// It displays alarm information on the LCD and transitions back to the previous operational state
// when the alarm condition is resolved.
class WindowAlarm : public State {
    public:
        WindowAlarm(State* state);
        void handle() override;
        State* next() override;
    private:
        State* prevState;
};

#endif
