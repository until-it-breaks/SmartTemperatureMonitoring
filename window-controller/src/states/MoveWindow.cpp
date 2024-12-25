#include "MoveWindow.h"
#include "IdleAuto.h"
#include "IdleManual.h"
#include "Context.h"
#include "controllers/WindowController.h"
#include <config.h>

void MoveWindow::handle() {
    context->getWindowController()->setLevel(context->getLevel());
}

State* MoveWindow::next() {
    if (context->getOperatingMode() == AUTO) {
        return new IdleAuto(context);
    }
    if (context->getOperatingMode() == MANUAL) {
        return new IdleManual(context);
    }
    return nullptr;
}
