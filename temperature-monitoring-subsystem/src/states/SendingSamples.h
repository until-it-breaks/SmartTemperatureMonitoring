#ifndef __SENDINGSAMPLES__
#define __SENDINGSAMPLES__

#include "State.h"
#include "controllers/TemperatureController.h"
#include <PubSubClient.h>


extern TemperatureController* tempController;
extern PubSubClient client;
extern const char* topic_samples;

extern bool isNetworkConnected;

class SendingSamples : public State {
    public:
        SendingSamples();
        void execute() override;
        State* next() override;
};

#endif