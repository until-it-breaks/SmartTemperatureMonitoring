#include "Idle.h"
#include "ProblemDetected.h"

Idle::Idle() {
}

void Idle::init() {
    ledController->switchOnGreen();
    ledController->switchOffRed();
}

State* Idle::handle() {
    if (!isNetworkConnected) {
        return new ProblemDetected();
    }
    return nullptr;
}