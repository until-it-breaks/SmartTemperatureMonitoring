#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* windowController;
extern LcdController* lcdController;

ActiveAuto::ActiveAuto(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveAuto::handle() {
    float level = context->getLevel();
    windowController->setLevel(level);
    lcdController->printInfo(level, "AUTO", context->getTemperature());
}

State* ActiveAuto::next() {
    return new IdleAuto(this);
}
