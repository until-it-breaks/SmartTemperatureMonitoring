#ifndef __LCD_CONTROLLER__
#define __LCD_CONTROLLER__

#include "LiquidCrystal_I2C.h"

class LcdController {
    private:
        LiquidCrystal_I2C* lcd;
    public:
        LcdController(LiquidCrystal_I2C* lcd);
        void turnOff();
        void turnOn();
        void printWelcome();
        void printInfo(float level, String mode);
        void printInfo(float level, String mode, float temperature);
        void printAlarmInfo();
};

#endif