#include "Idle.h"

Idle::Idle() {
}

void Idle::init() {
    ledController->switchOnGreen();
    ledController->switchOffRed();
}

State* Idle::handle() {

}