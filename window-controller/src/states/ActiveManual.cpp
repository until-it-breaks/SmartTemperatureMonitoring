#include "ActiveManual.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* windowController;
extern LcdController* lcdController;

ActiveManual::ActiveManual(State* state) {
    Serial.println("ActiveManual");
    if (state != nullptr) {
        delete state;
    }
}

void ActiveManual::handle() {
    Serial.println("Handling ActiveManual");
    windowController->setLevel(context->getLevel());
    lcdController->printInfo(context->getLevel(), "MANUAL");
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
