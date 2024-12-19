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
    float level = context->getLevel();
    windowController->setLevel(level);
    lcdController->printInfo(level, "MANUAL");
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
