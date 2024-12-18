#include "ReadPotentiometerTask.h"
#include "Context.h"
#include "Arduino.h"
#include "Config.h"

extern Context* context;

void ReadPotentiometerTask::tick() {
    if (context->getOperatingMode() == OperatingMode::MANUAL) {
        int sensorValue = analogRead(POTENTIOMETER_PIN);
        float outputValue = sensorValue / 1023.0;
        if (outputValue != context->getManualLevel()) {
            context->setManualLevel(outputValue);
        }
    }
}
