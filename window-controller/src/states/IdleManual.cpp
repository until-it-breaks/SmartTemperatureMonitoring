#include "IdleAuto.h"
#include "ActiveManual.h"
#include "IdleManual.h"

void IdleManual::handle() {
    // does nothing
}

State* IdleManual::next() {
    // after T1
    return new ActiveManual(this);
    // if button is pressed or operationMode == auto
    return new IdleManual(this);
}
