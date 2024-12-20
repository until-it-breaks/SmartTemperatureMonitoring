#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"

extern Context* context;
extern WindowController* windowController;

ActiveManual::ActiveManual(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveManual::handle() {
    windowController->setLevel(context->getLevel());
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
