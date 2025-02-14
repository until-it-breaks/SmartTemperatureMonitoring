# Assignment #03 - Smart Temperature Monitoring

## The project includes:

### 1. Temperature Monitoring subsystem (based on ESP)
- embedded system to monitor the temperature of the closed environment
- it interacts with the Control Room subsystem (via MQTT)
### 2. Control Unit subsystem  (backend - running on a PC)
- main subsystem, governing and coordinating  the whole system
- it interacts via MQTT with the Temperature Monitoring
- it interacts through the serial line with the Window Controller
- it interacts via HTTP with the Dashboard
### 3. Window Controller subsystem (Arduino)
- embedded system controlling the window (opening) and providing a panel for operators interaction in place
- it interacts via serial line with the Control Unit backend
### 4. Dashboard subsystem (Frontend/web app on the PC)
- front-end for operators to remotely visualise data and interact with the system
- it interacts via HTTP with the Control Unit backend

## Hardware components 

### Temperature Monitoring subsystem
SoC ESP32 board (or ESP8266) including
- 1 temperature sensor
- 1 green led 
- 1 red led
### Window Controller subsystem
Microcontroller Arduino UNO board including:
- 1 servo motor
- 1 potentiometer
- 1 tactile button
- 1 LCD display

## General Behaviour of the system

The system is meant to monitor the temperature of the closed environment and - depending on the value - control the opening of the window. It can be in two different modes: AUTOMATIC or MANUAL. In AUTOMATIC mode, the system automatically decides how much the window must be opened, depending on the current temperature. In MANUAL mode, the opening is controlled manually by an operator. The starting mode when booting is AUTOMATIC.
