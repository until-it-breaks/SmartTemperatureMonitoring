#include <Arduino.h>
#include "headers/pins.h"
#include "controllers/LedController.h"
#include "controllers/TemperatureController.h"

LedController* ledController;
TemperatureController* tempController;


void setup() {
  ledController = new LedController(new Led(GREEN_LED_PIN), new Led(RED_LED_PIN));
  tempController = new TemperatureController(new TemperatureSensor(TEMP_PIN));
}

void loop() {
  // put your main code here, to run repeatedly:
}

