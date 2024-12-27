#include "Idle.h"
#include "ProblemDetected.h"
#include "headers/defines.h"

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
    if (millis() - startTime >= SENDING_PERIOD) {
        
    }
    return nullptr;
}