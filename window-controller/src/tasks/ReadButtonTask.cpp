#include "ReadButtonTask.h"
#include "Context.h"
#include "Config.h"
#include "Arduino.h"

extern Context* context;

ReadButtonTask::ReadButtonTask() {
    this->button = new Button(BUTTON_PIN);
}

void ReadButtonTask::tick() {
    if (button->wasPressed()) {
        if (context->getOperatingMode() == OperatingMode::AUTO) {
            context->setOperatingMode(OperatingMode::MANUAL);
        } else {
            context->setOperatingMode(OperatingMode::AUTO);
        }
    }
}
