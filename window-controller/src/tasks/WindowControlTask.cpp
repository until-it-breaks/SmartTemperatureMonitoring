#include "WindowControlTask.h"
#include "states/IdleAuto.h"
#include "states/WindowOperational.h"

WindowControlTask::WindowControlTask(Context* context) {
    this->currentState = new WindowOperational(context, new IdleAuto(context));
    this->context = context;
}

void WindowControlTask::tick() {
    this->currentState->handle();
    State* next = this->currentState->next();
    if (next != currentState && next != nullptr) {
        delete this->currentState;
        this->currentState = next;
    }
}
