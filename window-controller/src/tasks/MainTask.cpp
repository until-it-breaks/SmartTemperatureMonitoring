#include "MainTask.h"
#include "states/IdleAuto.h"
#include "states/WindowOperational.h"

MainTask::MainTask() {
    this->currentState = new WindowOperational(new IdleAuto(nullptr));
    this->currentState->handle();
}

void MainTask::tick() {
    State* next = this->currentState->next();
    if (next != nullptr) {
        delete this->currentState;
        this->currentState = next;
        this->currentState->handle();
    }
}
