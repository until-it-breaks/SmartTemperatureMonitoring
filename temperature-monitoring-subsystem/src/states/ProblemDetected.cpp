#include "ProblemDetected.h"
#include "Idle.h"

ProblemDetected::ProblemDetected() {
}

void ProblemDetected::execute() {
    Serial.println("Problem State");
    ledController->switchOffGreen();
    ledController->switchOnRed();
}

State* ProblemDetected::next() {
    if (isNetworkConnected) {
        return new Idle();
    }
    return nullptr;
}