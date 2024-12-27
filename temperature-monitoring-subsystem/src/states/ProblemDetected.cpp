#include "ProblemDetected.h"
#include "Idle.h"

ProblemDetected::ProblemDetected() {
}

void ProblemDetected::init() {
    ledController->switchOffGreen();
    ledController->switchOnRed();
}

State* ProblemDetected::handle() {
    if (isNetworkConnected) {
        return new Idle();
    }
    return nullptr;
}