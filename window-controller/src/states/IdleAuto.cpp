#include "IdleAuto.h"
#include "ActiveAuto.h"
#include "IdleManual.h"
#include "Context.h"
#include "Config.h"
#include "controllers/LcdController.h"

extern LcdController* lcdController;

IdleAuto::IdleAuto(Context* context): State(context) {
    this->startTime = millis();
}

void IdleAuto::handle() {
    lcdController->printInfo(context->getLevel(), "AUTO", context->getTemperature());
}

State* IdleAuto::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new ActiveAuto(context);
    } else if (this->context->getOperatingMode() == OperatingMode::MANUAL) {
        return new IdleManual(context);
    } else {
        return this;
    }
}
