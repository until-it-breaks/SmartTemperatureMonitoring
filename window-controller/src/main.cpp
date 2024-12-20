#include <Arduino.h>
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"
#include "scheduler/Scheduler.h"
#include "Config.h"
#include "communication/MsgService.h"
#include "tasks/ReadPotentiometerTask.h"
#include "tasks/ReadButtonTask.h"
#include "tasks/WindowControlTask.h"

Context* context;
WindowController* windowController;
LcdController* lcdController;
Scheduler scheduler;

void setup() {
    MsgService.init();
    context = new Context();
    windowController = new WindowController(SERVO_PIN);
    lcdController = new LcdController(new LiquidCrystal_I2C(0x27, 16, 2));
    lcdController->printWelcome();
    scheduler.init(100);
    Task* readPotentiometerTask = new ReadPotentiometerTask(context);
    readPotentiometerTask->init(1000);
    Task* readButtonTask = new ReadButtonTask(context);
    readButtonTask->init(100);
    Task* windowControlTask = new WindowControlTask(context);
    windowControlTask->init(500);
    scheduler.addTask(readButtonTask);
    scheduler.addTask(readPotentiometerTask);
    scheduler.addTask(windowControlTask);
    delay(1000);
}

void loop() {
    scheduler.schedule();
}
