#include "LcdController.h"

LcdController::LcdController(LiquidCrystal_I2C* lcd) {
    this->lcd = lcd;
    this->lcd->init();
	this->lcd->backlight();
}

LcdController::~LcdController() {
    delete this->lcd;
}

void LcdController::printInfo(float level, String mode) {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Level:" + String(level * 100) + "%");
    this->lcd->setCursor(0,1);
    this->lcd->print("Mode:" + mode);
}

void LcdController::printInfo(float level, String mode, float temperature) {
    this->lcd->clear();
    this->lcd->setCursor(0,0);
    this->lcd->print("Level:" + String(level * 100) + "%");
    this->lcd->setCursor(0,1);
    this->lcd->print("Mode:" + mode + " " + String(temperature) + "C");
}

void LcdController::turnOff() {
    this->lcd->noBacklight();
    this->lcd->noDisplay();
}

void LcdController::turnOn() {
    this->lcd->display();
    this->lcd->backlight();
}