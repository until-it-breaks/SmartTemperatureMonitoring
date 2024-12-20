#include "IdleAuto.h"
#include "ActiveManual.h"
#include "WindowAlarm.h"
#include "WindowOperational.h"
#include "Context.h"
#include "controllers/LcdController.h"

extern Context* context;
extern LcdController* lcdController;

WindowAlarm::WindowAlarm(Context* context, State* state): State(context) {
    this->prevState = state;
}

void WindowAlarm::handle() {
    lcdController->printAlarmInfo();
}

State* WindowAlarm::next() {
    if (context->getSystemState() != SystemState::ALARM) {
        return new WindowOperational(context, prevState);
    } else {
        return this;
    }
}
