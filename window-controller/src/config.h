#ifndef __CONFIG__
#define __CONFIG__

// Pin configurations
const int SERVO_PIN = 3;
const int BUTTON_PIN = 2;
const int POTENTIOMETER_PIN = 14; // Equivalent of A0

// Window settings
const float WINDOW_MIN_LEVEL = 0.0;
const float WINDOW_MAX_LEVEL = 1.0;
// Timer 2 pulse width
const int WINDOW_CLOSED_WIDTH = 1500;
const int WINDOW_OPEN_WIDTH = 2250;

// Scheduler timings
const int SCHEDULER_PERIOD = 100;
const int POTENTIOMETER_PERIOD = 1000;
const int BUTTON_PERIOD = 100;
const int RECEIVE_MSG_PERIOD = 1000;
const int SEND_MSG_PERIOD = 1000;
const int WINDOW_PERIOD = 500;

// State timings
const unsigned long IDLE_TIME = 1000;

// The button has to be held for at least 50ms in order to register a press
const unsigned long DEBOUNCE_DELAY = 50;

// Operating modes / switch request modes
const int NONE = 0;
const int AUTO = 1;
const int MANUAL = 2;

#endif