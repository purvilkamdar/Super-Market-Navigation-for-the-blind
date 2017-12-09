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
- Super-Market-Navigation-for-the-blind
- |__ Extra: This directory contains Android Application project which have code that we developed as prior experiments
- |__ HardwareDevice: This directory contains Android Application project for sensor, compass, TTS and localization unit
- |__ NodeServer: NodeJS code for handling REST request and displaying data on Dashboard
- |__ Subscriber: Python code to subscribe value from Google PubSub
