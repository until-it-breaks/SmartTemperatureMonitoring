#include "MainTask.h"
#include "states/IdleAuto.h"
#include "states/WindowOperational.h"

MainTask::MainTask() {
    this->currentState = new WindowOperational(new IdleAuto(nullptr));
}

void MainTask::tick() {
    this->currentState->handle();
    State* next = this->currentState->next();
    if (next != nullptr) {
        delete this->currentState;
        this->currentState = next;
    }
}
