#ifndef __READ_POTENTIOMETER_TASK__
#define __READ_POTENTIOMETER_TASK__

#include "Task.h"
#include "components/Potentiometer.h"
#include "Context.h"

class ReadPotentiometerTask: public Task {
    public:
        ReadPotentiometerTask(Context* context);
        void tick() override;
    private:
        Potentiometer* potentiometer;
        Context* context;
};

#endif
