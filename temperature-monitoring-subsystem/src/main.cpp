#include <Arduino.h>
#include "headers/pins.h"
#include "controllers/LedController.h"
#include "controllers/TemperatureController.h"
#include "tasks/MeasuringTemperature.h"
#include <WiFi.h>
#include <PubSubClient.h>

// Wi-Fi credentials
const char* ssid = "OnePlus 11 5G";
const char* password = "alfabeta";

LedController* ledController;
TemperatureController* tempController;
TaskHandle_t Task1;

// MQTT broker details
const char* mqtt_server = "34.154.239.184";
const int mqtt_port = 1883;
const char* mqtt_topic = "esiot";

WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi();

void connect_to_mqtt();

/* MQTT subscribing callback */

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.println(String("Message arrived on [") + topic + "] len: " + length );
}

void setup() {
  Serial.begin(115200); 
  ledController = new LedController(new Led(GREEN_LED_PIN), new Led(RED_LED_PIN));
  tempController = new TemperatureController(new TemperatureSensor(TEMP_PIN));
  ledController->switchOnGreen();
  ledController->switchOnRed();

  setup_wifi();
  client.setServer(mqtt_server, mqtt_port);
  connect_to_mqtt();
  client.setCallback(callback);

  xTaskCreatePinnedToCore(measuringTemperatureTask, "measureTemperatureTask", 10000, NULL, 1, &Task1, 0);
}

void loop() {
  if (!client.connected()) {
      connect_to_mqtt();
  }
  client.loop();

  // Example: Publish a message
    // Creazione del messaggio JSON
  String jsonMessage = "{\"temperature\":23.5,\"humidity\":60}";

  // Pubblicazione del messaggio JSON
client.publish(mqtt_topic, jsonMessage.c_str());
  delay(5000);
}

void setup_wifi() {
    Serial.print("Connecting to Wi-Fi...");
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.print(".");
    }
    Serial.println("Connected to Wi-Fi");
}

void connect_to_mqtt() {
    while (!client.connected()) {
        Serial.print("Connecting to MQTT broker...");
        if (client.connect("ESP32Client")) {
            Serial.println("Connected");
            client.subscribe(mqtt_topic);
        } else {
            Serial.print("Failed. State: ");
            Serial.println(client.state());
            delay(5000);
        }
    }
}