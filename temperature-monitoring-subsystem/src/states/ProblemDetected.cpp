#include "ProblemDetected.h"
#include "Idle.h"

ProblemDetected::ProblemDetected() {
}

void ProblemDetected::execute() {
    ledController->switchOffGreen();
    ledController->switchOnRed();
}

State* ProblemDetected::next() {
    if (isNetworkConnected) {
        return new Idle();
    }
    return nullptr;
}