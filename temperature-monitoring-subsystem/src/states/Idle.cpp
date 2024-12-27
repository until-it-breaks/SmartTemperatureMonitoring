#include "Idle.h"
#include "ProblemDetected.h"

Idle::Idle() {
}

void Idle::execute() {
    ledController->switchOnGreen();
    ledController->switchOffRed();
}

State* Idle::next() {
    if (!isNetworkConnected) {
        return new ProblemDetected();
    }
    return nullptr;
}