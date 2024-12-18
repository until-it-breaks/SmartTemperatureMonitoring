#include "IdleAuto.h"
#include "ActiveManual.h"
#include "WindowOperational.h"
#include "WindowAlarm.h"
#include "Context.h"

extern Context* context;

WindowOperational::WindowOperational(State* state) {
    this->currentState = state;
}

void WindowOperational::handle() {
    this->currentState->handle();
}

State* WindowOperational::next() {
    if (context->getSystemState() != SystemState::ALARM) {
        State* next = currentState->next();
        if (next != nullptr) {
            this->currentState = next;
        } else {
            return nullptr;
        }
    } else {
        return new WindowAlarm(this->currentState);
    }
}
