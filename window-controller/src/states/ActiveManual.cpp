#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"

extern Context* context;
extern WindowController* windowController;

void ActiveManual::handle() {
    windowController->setLevel(context->getLevel());
}

State* ActiveManual::next() {
    return new IdleManual();
}
