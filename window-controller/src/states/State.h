#ifndef __STATE__
#define __STATE__

#include <Arduino.h>

class State {
    public:
        virtual void handle() = 0;
        virtual State* next() = 0; 
        virtual ~State() {}     
};

#endif