#include "Context.h"
#include "Config.h"

Context::Context() {
    this->temperature = 0.0f;
    this->level = 0.0f;
    this->mode = AUTO;
    this->needIntervention = false;
    this->modeToSwitchTo = NONE,
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
    this->needIntervention = true;
}

void Context::turnOffAlarmMode() {
    this->needIntervention = false;
}

float Context::getTemperature() {
    return this->temperature;
}

float Context::getLevel() {
    return this->level;
}

bool Context::requiresIntervention() {
    return this->needIntervention;
}

void Context::setModeToSwitchTo(int newMode) {
    this->modeToSwitchTo = newMode;
}

int Context::getModeToSwitchTo() {
    return this->modeToSwitchTo;
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
