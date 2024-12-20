#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"

extern WindowController* windowController;

void ActiveManual::handle() {
    windowController->setLevel(context->getLevel());
}

State* ActiveManual::next() {
    return new IdleManual(context);
}
