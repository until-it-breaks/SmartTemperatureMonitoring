#ifndef __CONFIG__
#define __CONFIG__

// Pin configurations
const int SERVO_PIN = 9;
const int BUTTON_PIN = 7;
const int POTENTIOMETER_PIN = 14; // Equivalent of A0

// Window settings
const float WINDOW_MIN_LEVEL = 0.0;
const float WINDOW_MAX_LEVEL = 1.0;
const int WINDOW_CLOSED_WIDTH = 1500;
const int WINDOW_OPEN_WIDTH = 2250;

// Scheduler configuration
const int SCHEDULER_PERIOD = 100;

// Idle time in milliseconds
const int IDLE_TIME = 1000;

#endif