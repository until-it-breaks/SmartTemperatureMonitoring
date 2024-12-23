#include "IdleAuto.h"
#include "ActiveManual.h"
#include "IdleManual.h"
#include "Config.h"
#include "Context.h"
#include "controllers/LcdController.h"

IdleManual::IdleManual(Context* context): State(context) {
    this->startTime = millis();
}

void IdleManual::handle() {
    context->getLcdController()->printInfo(context->getLevel(), "MANUAL", context->getTemperature());
}

State* IdleManual::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new ActiveManual(context);
    } else if (context->getOperatingMode() == AUTO) {
        return new IdleAuto(context);
    } else {
        return this;
    }
}
