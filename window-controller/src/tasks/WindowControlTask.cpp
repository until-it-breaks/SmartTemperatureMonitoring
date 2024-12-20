#include "WindowControlTask.h"
#include "states/IdleAuto.h"
#include "states/WindowOperational.h"

WindowControlTask::WindowControlTask() {
    this->currentState = new WindowOperational(new IdleAuto());
}

void WindowControlTask::tick() {
    this->currentState->handle();
    State* next = this->currentState->next();
    if (next != currentState) {
        delete this->currentState;
        this->currentState = next;
    }
}
