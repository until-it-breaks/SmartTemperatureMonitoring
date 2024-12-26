#include <Arduino.h>
#include "MeasuringTemperature.h"
#include "controllers/TemperatureController.h"

extern TemperatureController* tempController;

void measuringTemperatureTask(void* parameter) {
  Serial.print("Task1 is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    tempController->readTemp();
    delay(1000);
    Serial.println("current temperature: " + String(tempController->getTemp()));
    delay(1000);
  } 
}
