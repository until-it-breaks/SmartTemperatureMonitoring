#include "IdleAuto.h"
#include "ActiveManual.h"
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
    if (context->getSystemState() != SystemState::ALARM) {
        State* next = currentState->next();
        if (next != currentState) {
            delete currentState;
            this->currentState = next;
        }
        return this;
    } else {
        return new WindowAlarm(context, currentState);
    }
}
