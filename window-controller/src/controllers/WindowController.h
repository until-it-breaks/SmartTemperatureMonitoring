#ifndef __WINDOW_CONTROLLER__
#define __WINDOW_CONTROLLER__

#include <ServoTimer2.h>

class WindowController {
    public:
        WindowController(int pin);
        ~WindowController();
        void open();
        void close();
        void setLevel(float level);
        float getLevel();
    private:
        ServoTimer2* servo;
        float level;
};

#endif
