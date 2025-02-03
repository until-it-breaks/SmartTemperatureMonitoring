#include <Arduino.h>
#include "MeasuringTemperature.h"
#include "controllers/TemperatureController.h"
#include "headers/defines.h"

extern TemperatureController* tempController;

void measuringTemperatureTask(void* parameter) {
  for(;;){
    tempController->readTemp();
    delay(MONITORING_PERIOD);
    Serial.println("current temperature: " + String(tempController->getTemp()));
    delay(MONITORING_PERIOD);
  } 
}
