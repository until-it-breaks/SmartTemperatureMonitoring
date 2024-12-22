#include "LedController.h"

LedController::LedController(Led* greenLed, Led* redLed) {
    this->greenLed = greenLed;
    this->redLed = redLed;
}

LedController::~LedController() {
    delete this->greenLed;
    delete this->redLed;
}

void LedController::switchOnRed() {
    this->redLed->switchOn();
}

void LedController::switchOffRed() {
    this->redLed->switchOff();
}

void LedController::switchOnGreen() {
    this->greenLed->switchOn();
}

void LedController::switchOffGreen() {
    this->greenLed->switchOff();
}

bool LedController::isRedOn() {
    return this->redLed->isOn();
};

bool LedController::isGreenOn() {
    return this->greenLed->isOn();
}
