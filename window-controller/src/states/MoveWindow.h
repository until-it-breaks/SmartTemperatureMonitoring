#ifndef __MOVE_WINDOW__
#define __MOVE_WINDOW__

#include "State.h"

class MoveWindow : public State {
    public:
        MoveWindow(Context* context): State(context) {};
        void handle() override;
        State* next() override;
};

#endif
