#include "ReadButtonTask.h"
#include "Context.h"
#include "Config.h"
#include "Arduino.h"

ReadButtonTask::ReadButtonTask(Context* context) {
    this->button = new Button(BUTTON_PIN);
    this->context = context;
}

// Does not directly change the operating mode. Instead request the master (control-unit) to change it first and let it communicate the new mode to use.
void ReadButtonTask::tick() {
    if (button->wasPressed()) {
        if (context->getOperatingMode() == AUTO) {
            context->setModeToSwitchTo(MANUAL);
        } else if (context->getOperatingMode() == MANUAL) {
            context->setModeToSwitchTo(AUTO);
        }
    }
}
