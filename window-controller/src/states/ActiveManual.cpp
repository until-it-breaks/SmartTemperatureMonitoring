#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* window;
extern LcdController* lcd;

ActiveManual::ActiveManual(State* state) {
    if (state != nullptr) {
        delete state;
    }
}

void ActiveManual::handle() {
    window->setLevel(context->getManualLevel());
    lcd->printInfo(context->getManualLevel(), "MANUAL"); // TODO proper better
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
