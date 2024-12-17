#include "ActiveManual.h"
#include "IdleManual.h"

void ActiveManual::handle() {
    // windowOpeningLevel = potentiometerValue
    // report level, mode, temperature
}

State* ActiveManual::next() {
    return new IdleManual(this);
}
