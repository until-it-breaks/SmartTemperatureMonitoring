#include "Context.h"
#include "Config.h"

Context::Context() {
    this->temperature = 0;
    this->level = 0;
    this->mode = AUTO;
    this->alarm = false;
    this->lcdController = new LcdController(new LiquidCrystal_I2C(0x27, 16, 2));
    this->windowController = new WindowController(SERVO_PIN);
}

Context::~Context() {
    delete this->lcdController;
    delete this->windowController;
}

void Context::setTemperature(float temp) {
    this->temperature = temp;
}

void Context::setLevel(float level) {
    this->level = level;
}

void Context::setOperatingMode(int mode) {
    this->mode = mode;
}

void Context::turnOnAlarmMode() {
    this->alarm = true;
}

void Context::turnOffAlarmMode() {
    this->alarm = false;
}

float Context::getTemperature() {
    return this->temperature;
}

float Context::getLevel() {
    return this->level;
}

bool Context::requireIntervention() {
    return this->alarm;
}

int Context::getOperatingMode() {
    return this->mode;
}

LcdController* Context::getLcdController() {
    return this->lcdController;
}

WindowController* Context::getWindowController() {
    return this->windowController;
}
