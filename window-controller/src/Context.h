#include "controllers/LcdController.h"
#include "controllers/WindowController.h"
#ifndef __CONTEXT__
#define __CONTEXT__

class Context {
    public:
        Context();
        ~Context();
        void setTemperature(float temp);
        void setLevel(float level);
        void setOperatingMode(int mode);
        void turnOnAlarmMode();
        void turnOffAlarmMode();
        float getTemperature();
        float getLevel();
        bool requireIntervention();
        int getOperatingMode();
        LcdController* getLcdController();
        WindowController* getWindowController();
    private:
        float temperature;
        float level;
        bool alarm;
        int mode;
        LcdController* lcdController;
        WindowController* windowController;
};

#endif
