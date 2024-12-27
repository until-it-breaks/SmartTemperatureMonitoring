#ifndef __CONNECTION__
#define __CONNECTION__

#include "PubSubClient.h"

void connect_wifi(char* ssid, char* password);
void connect_to_mqtt(PubSubClient client);

#endif