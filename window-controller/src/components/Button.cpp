#include "Button.h"
#include "Arduino.h"
#include "Config.h"

Button::Button(int pin) {
    this->pin = pin;
    this->lastDebounceTime = 0;
    this->currentState = LOW;
    this->lastState = LOW;
    pinMode(pin, INPUT);
}

bool Button::wasPressed() {
    int reading = digitalRead(this->pin);

    if (reading != lastState) {
        lastDebounceTime = millis();
    }

    if ((millis() - lastDebounceTime) > DEBOUNCE_DELAY) {
        if (reading != currentState) {
            currentState = reading;
            if (currentState == HIGH) {
                return true;
            }
        }
    }
    lastState = reading;
    return false;
}
