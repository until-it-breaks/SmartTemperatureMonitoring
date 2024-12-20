#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"

extern WindowController* windowController;

void ActiveAuto::handle() {
    windowController->setLevel(context->getLevel());
}

State* ActiveAuto::next() {
    return new IdleAuto(context);
}
