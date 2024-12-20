#ifndef __STATE__
#define __STATE__

#include "Arduino.h"
#include "Context.h"

class State {
    public:
        State(Context* context) : context(context) {}
        virtual void handle() = 0;
        virtual State* next() = 0;
        virtual ~State() {};
    protected:
        Context* context;
};

#endif
