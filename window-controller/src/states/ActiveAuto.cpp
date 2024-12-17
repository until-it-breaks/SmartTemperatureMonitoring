#include "ActiveAuto.h"
#include "IdleAuto.h"

ActiveAuto::ActiveAuto(State* state) {
    delete state;
}

void ActiveAuto::handle() {
    // windowOpeningLevel = providedLevel
    // report level and mode
}

State* ActiveAuto::next() {
    return new IdleAuto(this);
}
