#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* window;
extern LcdController* lcd;

ActiveAuto::ActiveAuto(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveAuto::handle() {
    window->setLevel(context->getAutoLevel());
    lcd->printInfo(context->getAutoLevel(), "AUTO", context->getTemperature()); // TODO proper format
}

State* ActiveAuto::next() {
    return new IdleAuto(this);
}
