#include "ReadButtonTask.h"
#include "Context.h"
#include "Config.h"
#include "Arduino.h"

extern Context* context; // replace with interrupts

void ReadButtonTask::tick() {
    if (digitalRead(BUTTON_PIN) == HIGH) {
        if (context->getOperatingMode() == OperatingMode::AUTO) {
            context->setOperatingMode(OperatingMode::MANUAL);
        } else {
            context->setOperatingMode(OperatingMode::AUTO);
        }
    }
}
