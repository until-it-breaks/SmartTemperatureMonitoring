#include "Led.h"
#include <Arduino.h>

Led::Led(int pin) {
    this->pin = pin;
    this->on = false;
    pinMode(pin, OUTPUT);
}

void Led::switchOn() {
    this->on = true;
    digitalWrite(pin, HIGH);
}

void Led::switchOff() {
    this->on = false;
    digitalWrite(pin, LOW);
}

bool Led::isOn() {
    return this->on;
}
