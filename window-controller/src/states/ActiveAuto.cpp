#include "ActiveAuto.h"
#include "IdleAuto.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"

extern Context* context;
extern WindowController* windowController;
extern LcdController* lcdController;

ActiveAuto::ActiveAuto(State* state) {
    Serial.println("ActiveAuto");
    if (state != nullptr) {
        delete state;
    }
}

void ActiveAuto::handle() {
    Serial.println("Handling ActiveAuto");
    windowController->setLevel(context->getLevel());
    lcdController->printInfo(context->getLevel(), "AUTO", context->getTemperature());
}

State* ActiveAuto::next() {
    return new IdleAuto(this);
}
