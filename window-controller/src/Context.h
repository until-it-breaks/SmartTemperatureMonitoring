#ifndef __CONTEXT__
#define __CONTEXT__

#include <WString.h>

enum OperatingMode {AUTO, MANUAL};

enum SystemState {NORMAL, HOT, TOO_HOT, ALARM};

class Context {
    public:
        Context();
        void setTemperature(float temp);
        void setAutoLevel(float level);
        void setManualLevel(float level);
        void setOperatingMode(OperatingMode mode);
        void setSystemState(SystemState state);
        float getTemperature();
        float getAutoLevel();
        float getManualLevel();
        OperatingMode getOperatingMode();
        SystemState getSystemState();
    private:
        float temperature;
        float autoLevel;
        float manualLevel;
        OperatingMode mode;
        SystemState state;
};

#endif