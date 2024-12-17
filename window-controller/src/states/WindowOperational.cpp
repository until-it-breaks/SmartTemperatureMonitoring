#include "IdleAuto.h"
#include "ActiveManual.h"
#include "WindowOperational.h"
#include "WindowAlarm.h"

WindowOperational::WindowOperational(State* state) {
    this->currentState = state;
}

void WindowOperational::handle() {
    // does nothing
}

State* WindowOperational::next() {
    // if system state != alarm
    this->currentState = currentState->next();
    return nullptr;
    // else
    return new WindowAlarm(this->currentState);
}
