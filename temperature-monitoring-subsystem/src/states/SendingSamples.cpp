#include "SendingSamples.h"
#include "ProblemDetected.h"
#include "Idle.h"


SendingSamples::SendingSamples() {
}

void SendingSamples::execute() {
    String jsonMessage = "{\"temperature\":" + String(tempController->getTemp()) + "}";
    // Pubblicazione del messaggio JSON
    client.publish(topic_samples, jsonMessage.c_str());
}

State* SendingSamples::next() {
    if (!isNetworkConnected) {
        return new ProblemDetected();
    }
    return nullptr;
}