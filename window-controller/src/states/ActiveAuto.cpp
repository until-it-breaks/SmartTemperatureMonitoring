#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"

void ActiveAuto::handle() {
    context->getWindowController()->setLevel(context->getLevel());
}

State* ActiveAuto::next() {
    return new IdleAuto(context);
}
