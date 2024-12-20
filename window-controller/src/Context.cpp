#include "Context.h"

Context::Context() {
    this->temperature = 0;
    this->level = 0;
    this->mode = OperatingMode::AUTO;
    this->state = SystemState::NORMAL;
}

void Context::setTemperature(float temp) {
    this->temperature = temp;
}

void Context::setLevel(float level) {
    this->level = level;
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

float Context::getLevel() {
    return this->level;
}

OperatingMode Context::getOperatingMode() {
    return this->mode;
}

SystemState Context::getSystemState() {
    return this->state;
}
