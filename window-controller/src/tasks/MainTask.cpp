#include "MainTask.h"
#include "states/IdleManual.h"
#include "states/WindowOperational.h"

MainTask::MainTask() {
    this->currentState = new WindowOperational(new IdleManual(nullptr)); // To be changed to auto
    this->currentState->handle();
}

void MainTask::tick() {
    State* next = this->currentState->next();
    if (next != nullptr) {
        this->currentState = next;
        this->currentState->handle();
    } 
}
