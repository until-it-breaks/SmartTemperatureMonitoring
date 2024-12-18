#include "ReadPotentiometerTask.h"
#include "Context.h"
#include "Arduino.h"
#include "Config.h"

extern Context* context;

void ReadPotentiometerTask::tick() {
    if (context->getOperatingMode() == OperatingMode::MANUAL) {
        int sensorValue = analogRead(POTENTIOMETER_PIN);
        int outputValue = map(sensorValue, 0, 1023, 0.0, 1.0);
        if (outputValue != context->getAutoLevel()) {
            context->setManualLevel(outputValue);
        }
    }
}
