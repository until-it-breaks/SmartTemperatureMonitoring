#ifndef __TEMP_SENSOR__
#define __TEMP_SENSOR__

class TemperatureSensor {
    public:
        TemperatureSensor(int pin);
        int getTemp();
    private:
        int pin;
};

#endif
