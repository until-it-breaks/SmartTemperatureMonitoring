#include "IdleAuto.h"
#include "ActiveManual.h"
#include "IdleManual.h"
#include "Config.h"
#include "Context.h"

extern Context* context;

IdleManual::IdleManual(State* state) {
    Serial.println("IdleManual");
    if (state != nullptr) {
        delete state;
    }
    this->startTime = millis();
}

void IdleManual::handle() {
}

State* IdleManual::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new ActiveManual(this);
    } else if (context->getOperatingMode() == OperatingMode::AUTO) {
        return new IdleAuto(this);
    } else {
        return nullptr;
    }
}
