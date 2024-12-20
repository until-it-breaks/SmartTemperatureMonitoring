#include "IdleAuto.h"
#include "ActiveAuto.h"
#include "IdleManual.h"
#include "Context.h"
#include "Config.h"

extern Context* context;

IdleAuto::IdleAuto(State* state) {
    Serial.println("IdleAuto");
    if (state != nullptr) {
        delete state;
    }
    this->startTime = millis();
}

void IdleAuto::handle() {
}

State* IdleAuto::next() {
    if ((millis() - this->startTime) > IDLE_TIME) {
        return new ActiveAuto(this);
    } else if (context->getOperatingMode() == OperatingMode::MANUAL) {
        return new IdleManual(this);
    } else {
        return nullptr;
    }
}
