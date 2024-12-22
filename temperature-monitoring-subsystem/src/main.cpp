#include <Arduino.h>
#include "headers/pins.h"
#include "controllers/LedController.h"
#include "controllers/TemperatureController.h"

LedController* ledController;
TemperatureController* tempController;
TaskHandle_t Task1;

void Task1code(void* parameter);

void setup() {
  Serial.begin(115200); 
  ledController = new LedController(new Led(GREEN_LED_PIN), new Led(RED_LED_PIN));
  tempController = new TemperatureController(new TemperatureSensor(TEMP_PIN));
  ledController->switchOnGreen();
  ledController->switchOnRed();

  xTaskCreatePinnedToCore(Task1code, "Task1", 10000, NULL, 1, &Task1, 0);       
}


void Task1code(void* parameter){
  Serial.print("Task1 is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    tempController->readTemp();
    delay(500);
    Serial.println("current temperature: " + String(tempController->getTemp()));
    delay(500);
  } 
}


void loop() {
  delay(1000);
}

