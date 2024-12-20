#include "LcdController.h"

LcdController::LcdController(LiquidCrystal_I2C* lcd) {
    this->lcd = lcd;
    this->lcd->init();
	this->lcd->backlight();
}

void LcdController::printWelcome() {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Starting...");
}

void LcdController::printInfo(float level, String mode) {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Level:" + String(int(level * 100)) + "%");
    this->lcd->setCursor(0,1);
    this->lcd->print(mode);
}

void LcdController::printInfo(float level, String mode, float temperature) {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Level: " + String(int(level * 100)) + "%");
    this->lcd->setCursor(0,1);
    this->lcd->print(mode + " Temp: " + String(int(temperature)) + "C");
}

void LcdController::printAlarmInfo() {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Alert! Contact");
    this->lcd->setCursor(0,1);
    this->lcd->print("An Operator");
}

void LcdController::turnOff() {
    this->lcd->noBacklight();
    this->lcd->noDisplay();
}

void LcdController::turnOn() {
    this->lcd->display();
    this->lcd->backlight();
}