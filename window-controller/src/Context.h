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
        bool requiresIntervention();
        void setModeToSwitchTo(int newMode);
        int getModeToSwitchTo();
        int getOperatingMode();
        LcdController* getLcdController();
        WindowController* getWindowController();
    private:
        float temperature;
        float level;
        bool needIntervention;
        int mode;
        int modeToSwitchTo;
        LcdController* lcdController;
        WindowController* windowController;
};

#endif
