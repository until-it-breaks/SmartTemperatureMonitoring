#ifndef __LED_CONTROLLER__
#define __LED_CONTROLLER__

#include "components/Led.h"

class LedController {

    public:
        LedController(Led* greenLed, Led* redLed);
        ~LedController();
        void switchOnRed();
        void switchOffRed();
        void switchOnGreen();
        void switchOffGreen();
        bool isRedOn();
        bool isGreenOn();
    private:
        Led* greenLed;
        Led* redLed;
};

#endif
