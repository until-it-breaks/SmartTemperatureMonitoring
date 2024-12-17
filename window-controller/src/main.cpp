#include <Arduino.h>
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"
#include "scheduler/Scheduler.h"
#include "Config.h"
#include "communication/MsgService.h"

Context* context;
WindowController* windows;
LcdController* lcdController;
Scheduler scheduler;

void setup() {
    context = new Context();
    MsgService.init();
    scheduler.init(SCHEDULER_PERIOD);
}

void loop() {

}
