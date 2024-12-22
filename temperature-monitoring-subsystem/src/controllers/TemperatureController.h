#ifndef __TEMP_CONTROLLER__
#define __TEMP_CONTROLLER__

#include "components/TemperatureSensor.h"

class TemperatureController {
    public:
        TemperatureController(TemperatureSensor* sensor);
        ~TemperatureController();
        void readTemp();
        int getTemp();

    private:
        TemperatureSensor* tempSensor;
        int currentTemp;
        int tempThreshold;
};

#endif
