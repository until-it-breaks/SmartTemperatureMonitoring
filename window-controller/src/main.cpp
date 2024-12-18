#include <Arduino.h>
#include "Context.h"
#include "controllers/WindowController.h"
#include "controllers/LcdController.h"
#include "scheduler/Scheduler.h"
#include "Config.h"
#include "communication/MsgService.h"
#include "tasks/ReadPotentiometerTask.h"
#include "tasks/MainTask.h"

Context* context = new Context();
WindowController* windowController = new WindowController(SERVO_PIN);
LcdController* lcdController = new LcdController(new LiquidCrystal_I2C(0x27, 16, 2));
Scheduler scheduler;

void setup() {
    Serial.begin(9600); // To be removed
    scheduler.init(SCHEDULER_PERIOD);
    Task* readPotentiometer = new ReadPotentiometerTask();
    readPotentiometer->init(1000);
    Task* mainTask = new MainTask();
    mainTask->init(500);
    scheduler.addTask(readPotentiometer);
    scheduler.addTask(mainTask);
    Serial.println("Setup done");
}

void loop() {
    scheduler.schedule();
}
