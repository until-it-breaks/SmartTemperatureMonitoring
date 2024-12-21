#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"

void ActiveManual::handle() {
    context->getWindowController()->setLevel(context->getLevel());
}

State* ActiveManual::next() {
    return new IdleManual(context);
}
