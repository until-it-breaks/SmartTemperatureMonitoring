#include <Arduino.h>
#include "MeasuringTemperature.h"
#include "controllers/TemperatureController.h"
#include "headers/defines.h"

extern TemperatureController* tempController;

void measuringTemperatureTask(void* parameter) {
  Serial.print("Task1 is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    tempController->readTemp();
    delay(MONITORING_PERIOD);
    Serial.println("current temperature: " + String(tempController->getTemp()));
    delay(MONITORING_PERIOD);
  } 
}
