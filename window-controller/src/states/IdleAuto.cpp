#include "IdleAuto.h"
#include "ActiveAuto.h"
#include "IdleManual.h"

void IdleAuto::handle() {
    // does nothing
}

State* IdleAuto::next() {
    // after T1
    return new ActiveAuto(this);
    // if button is pressed or operationMode == manual
    return new IdleManual(this);
}
