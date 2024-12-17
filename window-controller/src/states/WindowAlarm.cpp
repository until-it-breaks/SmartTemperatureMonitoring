#include "IdleAuto.h"
#include "ActiveManual.h"
#include "WindowAlarm.h"
#include "WindowOperational.h"

WindowAlarm::WindowAlarm(State* state) {
    this->currentState = state;
}

void WindowAlarm::handle() {
    // does nothing
}

State* WindowAlarm::next() {
    // if system state != alarm
    return new WindowOperational(this->currentState);
}
