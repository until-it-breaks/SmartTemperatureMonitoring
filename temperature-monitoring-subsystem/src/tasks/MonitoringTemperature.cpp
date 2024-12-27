#include <Arduino.h>
#include "MonitoringTemperature.h"
#include "controllers/LedController.h"
#include "controllers/TemperatureController.h"
#include "states/Idle.h"
#include "states/State.h"
#include "headers/defines.h"

void monitoring(void* parameter) {
    State* currentState = new Idle();
    while(1) {
        delay(MONITORING_PERIOD);
        State* nextState = currentState->handle();
        if (nextState != nullptr) {
            currentState = nextState;
            currentState->init();
        }
    }
}