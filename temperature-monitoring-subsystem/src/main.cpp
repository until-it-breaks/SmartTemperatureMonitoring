#include <Arduino.h>
#include "headers/pins.h"
#include "controllers/LedController.h"
#include "controllers/TemperatureController.h"
#include "tasks/MeasuringTemperature.h"

LedController* ledController;
TemperatureController* tempController;
TaskHandle_t Task1;

void setup() {
  Serial.begin(115200); 
  ledController = new LedController(new Led(GREEN_LED_PIN), new Led(RED_LED_PIN));
  tempController = new TemperatureController(new TemperatureSensor(TEMP_PIN));
  ledController->switchOnGreen();
  ledController->switchOnRed();

  xTaskCreatePinnedToCore(measuringTemperatureTask, "measureTemperatureTask", 10000, NULL, 1, &Task1, 0);
}

void loop() {
  delay(1000);
}

