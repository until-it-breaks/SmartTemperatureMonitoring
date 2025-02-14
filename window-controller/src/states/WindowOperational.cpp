#include "IdleAuto.h"
#include "WindowOperational.h"
#include "WindowAlarm.h"
#include "Context.h"

WindowOperational::WindowOperational(Context* context, State* state): State(context) {
    this->currentState = state;
}

void WindowOperational::handle() {
    this->currentState->handle();
}

State* WindowOperational::next() {
    if (!context->requiresIntervention()) {
        State* next = currentState->next();
        if (next != currentState && next != nullptr) {
            delete currentState;
            this->currentState = next;
        }
        return this;
    } else {
        return new WindowAlarm(context, currentState);
    }
}
