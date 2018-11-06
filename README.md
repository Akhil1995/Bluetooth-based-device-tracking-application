# Bluetooth-based-device-tracking-application
Inspired by anti-theft affirmative actions, this project is developed to be used as a device tracker application, which can track all bluetooth devices around it, within a radius of 10m.

Written in Java, this project makes use of the open-source JSR-82 implementation - the Bluecove library. This application also makes use of various concepts such as multithreading, thread locks and arrayblockingqueues, to seamlessly collect,store and process available data. Running it brings up an interactive UI screen(written using JFrames), which can be used to track select devices available in the radius of around 10m.

Tracking a device creates a file corresponding to that device on the OS, which contains timestamps regarding when the device was within range and when it went out of range

## Installation
Clone this repository and run a maven clean install, to get an executable jar-with-dependencies.jar file.
```
mvn clean install
```
### Windows Environments
Application needs JRE 1.7 to run. All defaults are already installed in this OS
### Linux Environments
The application needs `libbluetooth-dev` program to be installed on the OS
For debian based systems like Ubuntu, run the following command
```
apt-get install libbluetooth-dev
```
For fedora based systems like Centos and redhat, run the following command
```
yum install libbluetooth-dev
```

## Running the application
Switch on bluetooth on the device the application is going to run on and execute the executable jar-with-dependencies.jar created in the target folder, post running clean install

An interactive UI screen will pop up, showing the list of devices available around, to be tracked
```
java -jar bluetooth-device-discovery-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```