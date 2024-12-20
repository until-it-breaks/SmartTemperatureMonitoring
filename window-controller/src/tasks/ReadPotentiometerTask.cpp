#include "ReadPotentiometerTask.h"
#include "Context.h"
#include "Arduino.h"
#include "Config.h"

ReadPotentiometerTask::ReadPotentiometerTask(Context* context) {
    this->potentiometer = new Potentiometer(POTENTIOMETER_PIN);
    this->context = context;
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
