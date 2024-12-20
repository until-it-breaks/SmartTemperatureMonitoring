#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"

extern Context* context;
extern WindowController* windowController;

ActiveAuto::ActiveAuto(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveAuto::handle() {
    windowController->setLevel(context->getLevel());
}

State* ActiveAuto::next() {
    return new IdleAuto(this);
}
