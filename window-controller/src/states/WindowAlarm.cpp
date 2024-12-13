#include "IdleAuto.h"
#include "ActiveManual.h"
#include "WindowAlarm.h"
#include "WindowOperational.h"
#include "Context.h"
#include "controllers/LcdController.h"

extern Context* context;
extern LcdController* lcdController;

WindowAlarm::WindowAlarm(State* state) {
    this->prevState = state;
}

void WindowAlarm::handle() {
    lcdController->printAlarmInfo();
}

State* WindowAlarm::next() {
    if (context->getSystemState() != SystemState::ALARM) {
        return new WindowOperational(this->prevState);
    } else {
        return nullptr;
    }
}
