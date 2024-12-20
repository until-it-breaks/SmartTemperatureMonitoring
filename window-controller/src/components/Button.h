#ifndef __BUTTON__
#define __BUTTON__

class Button {
    public:
        Button(int pin);
        bool wasPressed();
    private:
        int pin;
        unsigned long lastDebounceTime;
        bool lastState;
        bool currentState;
};

#endif
