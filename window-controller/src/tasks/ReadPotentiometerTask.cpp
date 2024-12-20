#include "ReadPotentiometerTask.h"
#include "Context.h"
#include "Arduino.h"
#include "Config.h"

extern Context* context;

void ReadPotentiometerTask::tick() {
    if (context->getOperatingMode() == OperatingMode::MANUAL) {
        int sensorValue = analogRead(POTENTIOMETER_PIN);
        float outputValue = sensorValue / 1023.0f;
        Serial.println(outputValue);
        if (outputValue != context->getLevel()) {
            context->setLevel(outputValue);
        }
    }
}
