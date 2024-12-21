#include "Arduino.h"
#include "Context.h"
#include "scheduler/Scheduler.h"
#include "Config.h"
#include "communication/MsgService.h"
#include "tasks/ReadPotentiometerTask.h"
#include "tasks/ReadButtonTask.h"
#include "tasks/WindowControlTask.h"
#include "tasks/ReceiveMsgTask.h"
#include "tasks/SendMsgTask.h"

Context* context;
Scheduler scheduler;

void setup() {
    MsgService.init();
    context = new Context();
    context->getLcdController()->printWelcome();
    scheduler.init(100);
    Task* readPotentiometerTask = new ReadPotentiometerTask(context);
    readPotentiometerTask->init(POTENTIOMETER_PERIOD);
    Task* readButtonTask = new ReadButtonTask(context);
    readButtonTask->init(BUTTON_PERIOD);
    Task* receiveMsgTask= new ReceiveMsgTask(context);
    receiveMsgTask->init(RECEIVE_MSG_PERIOD);
    Task* sendMsgTask = new SendMsgTask(context);
    sendMsgTask->init(SEND_MSG_PERIOD);
    Task* windowControlTask = new WindowControlTask(context);
    windowControlTask->init(WINDOW_PERIOD);
    scheduler.addTask(readButtonTask);
    scheduler.addTask(readPotentiometerTask);
    scheduler.addTask(receiveMsgTask);
    scheduler.addTask(sendMsgTask);
    scheduler.addTask(windowControlTask);
    delay(1000);
}

void loop() {
    scheduler.schedule();
}
