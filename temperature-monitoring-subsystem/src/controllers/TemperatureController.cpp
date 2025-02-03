#include "TemperatureController.h"
#include "headers/Defines.h"

TemperatureController::TemperatureController(TemperatureSensor* sensor) {
    this->tempSensor = sensor;
    this->currentTemp = 0;
}

TemperatureController::~TemperatureController() {
    delete this->tempSensor;
}

/**
 * Reads the temp.
 */
void TemperatureController::readTemp() {
    this->currentTemp = this->tempSensor->getTemp();
}

/**
 * Returns the last read value. In order to get the latest value readTemp() should be called first.
 */
int TemperatureController::getTemp() {
    return this->currentTemp;
}

