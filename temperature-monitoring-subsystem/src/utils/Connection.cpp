#include "Connection.h"
#include "WIFI.h"

extern bool isNetworkConnected;

void connect_wifi(char* ssid, char* password) {
    Serial.print("Connecting to Wi-Fi...");
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.print(".");
    }
    Serial.println("Connected to Wi-Fi");
}

void connect_to_mqtt(PubSubClient client) {
    while (!client.connected()) {
        Serial.print("Connecting to MQTT broker...");
        if (client.connect("ESP32Client")) {
            Serial.println("Connected");
            isNetworkConnected = true;
        } else {
            Serial.print("Failed. State: ");
            Serial.println(client.state());
            delay(5000);
        }
    }
}
