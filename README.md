Super Market Navigation for Visually Impaired   Date: December 8, 2017
======================================================================

DESCRIPTION
-----------
- It is android things based IoT application which is developed on Raspberry Pi 3 platform.
- It contains various modules
  1. Sensor and localization module which runs on Android Things OS on Raspberry Pi 3
  2. Pedometer module which runs on SJOne board
  3. Subscriber module which is implemented in Python and interacts with Google PubSub
  4. Node Server which handles REST requests and display values on dashboard.
  
DIRECTORY STRUCTURE
-------------------
Super-Market-Navigation-for-the-blind<br />
|__ Extra: This directory contains Android Application project which have code that we developed as prior experiments<br />
|__ HardwareDevice: This directory contains Android Application project for sensor, compass, TTS and localization unit<br />
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__ src: Contains implemented Java classes <br />
|__ NodeServer: NodeJS code for handling REST request and displaying data on Dashboard<br />
|__ Subscriber: Python code to subscribe value from Google PubSub<br />
|__ Pedometer: C and C++ code running on FreeRTOS for step detection<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__ L5_Application: Contains source code for step detection algorithm main file<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__ periodic_scheduler: Contains accelerometer data processing and Kalman filter code<br />
