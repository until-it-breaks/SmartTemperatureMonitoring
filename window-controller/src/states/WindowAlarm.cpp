#include "IdleAuto.h"
#include "WindowAlarm.h"
#include "WindowOperational.h"
#include "Context.h"
#include "controllers/LcdController.h"

WindowAlarm::WindowAlarm(Context* context, State* state): State(context) {
    this->prevState = state;
}

void WindowAlarm::handle() {
    context->getLcdController()->printAlarmInfo();
}

State* WindowAlarm::next() {
    if (!context->requiresIntervention()) {
        return new WindowOperational(context, prevState);
    } else {
        return this;
    }
}
