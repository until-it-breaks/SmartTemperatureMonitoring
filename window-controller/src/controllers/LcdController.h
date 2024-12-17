#ifndef __LCD_CONTROLLER__
#define __LCD_CONTROLLER__

#include "LiquidCrystal_I2C.h"

class LcdController {
    private:
        LiquidCrystal_I2C* lcd;
    public:
        LcdController(LiquidCrystal_I2C* lcd);
        ~LcdController();
        void turnOff();
        void turnOn();
        void printInfo(float level, String mode);
        void printInfo(float level, String mode, float temperature);
};

#endif