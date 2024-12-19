#ifndef __CONTEXT__
#define __CONTEXT__

#include <WString.h>

enum OperatingMode {AUTO, MANUAL};

enum SystemState {NORMAL, HOT, TOO_HOT, ALARM};

class Context {
    public:
        Context();
        void setTemperature(float temp);
        void setLevel(float level);
        void setOperatingMode(OperatingMode mode);
        void setSystemState(SystemState state);
        float getTemperature();
        float getLevel();
        OperatingMode getOperatingMode();
        SystemState getSystemState();
    private:
        float temperature;
        float level;
        OperatingMode mode;
        SystemState state;
};

#endif