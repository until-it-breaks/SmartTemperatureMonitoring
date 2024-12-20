#include "ReadPotentiometerTask.h"
#include "Context.h"
#include "Arduino.h"
#include "Config.h"

extern Context* context;

ReadPotentiometerTask::ReadPotentiometerTask() {
    this->potentiometer = new Potentiometer(POTENTIOMETER_PIN);
}

void ReadPotentiometerTask::tick() {
    if (context->getOperatingMode() == OperatingMode::MANUAL) {
        int sensorValue = potentiometer->getValue();
        float outputValue = sensorValue / 1023.0f;
        if (outputValue != context->getLevel()) {
            context->setLevel(outputValue);
        }
    }
}
