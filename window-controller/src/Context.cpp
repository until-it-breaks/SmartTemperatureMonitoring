#include "Context.h"

Context::Context() {
    this->temperature = 0;
    this->autoLevel = 0;
    this->manualLevel = 0;
    this->mode = OperatingMode::MANUAL;
    this->state = SystemState::NORMAL;
}

void Context::setTemperature(float temp) {
    this->temperature = temp;
}

void Context::setAutoLevel(float level) {
    this->autoLevel = level;
}

void Context::setManualLevel(float level) {
    this->manualLevel = level;
}

void Context::setOperatingMode(OperatingMode mode) {
    this->mode = mode;
}

void Context::setSystemState(SystemState state) {
    this->state = state;
}

float Context::getTemperature() {
    return this->temperature;
}

float Context::getAutoLevel() {
    return this->autoLevel;
}

float Context::getManualLevel() {
    return this->manualLevel;
}

OperatingMode Context::getOperatingMode() {
    return this->mode;
}

SystemState Context::getSystemState() {
    return this->state;
}
