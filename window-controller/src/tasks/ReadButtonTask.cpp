#include "ReadButtonTask.h"
#include "Context.h"
#include "Config.h"
#include "Arduino.h"

ReadButtonTask::ReadButtonTask(Context* context) {
    this->button = new Button(BUTTON_PIN);
    this->context = context;
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
