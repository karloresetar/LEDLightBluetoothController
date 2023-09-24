# Mobile application for controlling the LED lights of the Arduino Uno board

This mobile application was made as a graduation thesis for my bachelor's degree.

## Table of contents

  - [Project Information](#Project-information)
  - [Features](#Features)
  - [Tech stack](Tech-stack)
  - [Authors](Authors)


### Project information
This is a project made for the "final thesis" on the University of Split. It attempts to demonstrate an Android mobile application created using Kotlin programming language to control the light-emitting diode of the Arduino Uno board. The application works in a way by establishing a connection between the smartphone and the Arduino Uno board using the Bluetooth SPP (Serial Port Protocol) module HC-05, which is designed for transparent wireless serial communication, and enables control of the light emitting diode.


### Features

  - Enabling connection to bluetooth devices
  - Controlling LED light in multiple ways
      - TURN ON LED
      - TURN OFF LED
      - BLINK LED
      - Brightness Control by sending desired value between 0-255
      - Controlling Brightness by using SeekBar Component ( Slider )


## Required Componenets

  - Arduino UNO
  - LED diode
  - Bluetooth Module HC-05
  - Breadboard
  - Jumper Wires

## Tech stack

  - Arduino IDE
  - Android Studio
  - Kotlin


## Authors

Karlo Rešetar [@karloresetar](https://github.com/karloresetar)
