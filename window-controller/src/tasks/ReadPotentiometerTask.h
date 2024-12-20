#ifndef __READ_POTENTIOMETER_TASK__
#define __READ_POTENTIOMETER_TASK__

#include "Task.h"
#include "components/Potentiometer.h"

class ReadPotentiometerTask: public Task {
    public:
        ReadPotentiometerTask();
        void tick() override;
    private:
        Potentiometer* potentiometer;
};

#endif
