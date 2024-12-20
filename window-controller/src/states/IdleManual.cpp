#include "IdleAuto.h"
#include "ActiveManual.h"
#include "IdleManual.h"
#include "Config.h"
#include "Context.h"
#include "controllers/LcdController.h"

extern LcdController* lcdController;

IdleManual::IdleManual(Context* context): State(context) {
    this->startTime = millis();
}

void IdleManual::handle() {
    lcdController->printInfo(context->getLevel(), "MANUAL");
}

State* IdleManual::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new ActiveManual(context);
    } else if (context->getOperatingMode() == OperatingMode::AUTO) {
        return new IdleAuto(context);
    } else {
        return this;
    }
}
