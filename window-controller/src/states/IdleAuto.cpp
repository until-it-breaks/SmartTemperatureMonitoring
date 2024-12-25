#include "IdleAuto.h"
#include "IdleManual.h"
#include "MoveWindow.h"
#include "Context.h"
#include "Config.h"
#include "controllers/LcdController.h"

IdleAuto::IdleAuto(Context* context): State(context) {
    this->startTime = millis();
}

void IdleAuto::handle() {
    context->getLcdController()->printInfo(context->getLevel(), "AUTO", context->getTemperature());
}

State* IdleAuto::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new MoveWindow(context);
    } else if (this->context->getOperatingMode() == MANUAL) {
        return new IdleManual(context);
    } else {
        return this;
    }
}
