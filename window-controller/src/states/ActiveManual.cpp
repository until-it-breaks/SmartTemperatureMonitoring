#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* windowController;
extern LcdController* lcdController;

ActiveManual::ActiveManual(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveManual::handle() {
    windowController->setLevel(context->getManualLevel());
    lcdController->printInfo(context->getManualLevel(), "MANUAL"); // TODO proper better
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
