## Mobile application for controlling the LED lights of the Arduino Uno board
This mobile application was made as a graduation thesis for my bachelor's degree.

---

Layout to select the device you want to connect to.

<img src="https://github.com/karloresetar/LEDLightBluetoothController/assets/39807142/26e155c6-5cdb-4bc0-b5b1-7874fc134e1d" width="200" height="400">

---

Layout which allows to control LED

<img src="https://github.com/karloresetar/LEDLightBluetoothController/assets/39807142/89560550-8cbb-4373-8caf-12caa139ecda" width="200" height="400">


## Table of contents

  - [Project Information](#Project-information)
  - [Features](#Features)
  - [Required Components](#Required-Components)
  - [Tech stack](#Tech-stack)
  - [Arduino Code](#Arduino-Code)
  - [Author](#Author)


### Project information
This is a project made for the "final thesis" on the University of Split. It attempts to demonstrate an Android mobile application created using Kotlin programming language to control the light-emitting diode of the Arduino Uno board. The application works in a way by establishing a connection between the smartphone and the Arduino Uno board using the Bluetooth SPP (Serial Port Protocol) module HC-05, which is designed for transparent wireless serial communication, and enables control of the light emitting diode.


### Features

  - Enabling connection to bluetooth devices
  - Controlling LED light in multiple ways
      - TURN LED ON
      - TURN LED OFF
      - BLINK LED
      - Brightness Control by sending desired value between 0-255
      - Brightness Control by using SeekBar Component ( Slider )


### Required Components

  - Arduino UNO
  - LED diode
  - Bluetooth Module HC-05
  - Breadboard
  - Jumper Wires

### Tech stack

  - [Arduino IDE](https://www.arduino.cc/en/software)
  - [Android Studio](https://developer.android.com/studio)
  - [Kotlin](https://kotlinlang.org/)
  - [ArduinoJson](https://arduinojson.org/)

### Arduino Code

```
#include <ArduinoJson.h>
int data;

void setup()
{
  Serial.begin(9600);
  pinMode(6, OUTPUT);
}

void loop() {
  while (Serial.available() > 0) {
    String jsonData = Serial.readStringUntil('\n');
    
    StaticJsonDocument<255> doc;
    DeserializationError error = deserializeJson(doc, jsonData);

    if (error) {
      Serial.println("JSON parsing error");
      return;
    }

    String command = doc["command"];
    int value = doc["value"];

    if (command == "turn_on") {
      digitalWrite(6, HIGH);
    } else if (command == "turn_off") {
      digitalWrite(6, LOW);
    } else if (command == "blink") {
      blinkLED(6);
    } else if (command == "set_brightness") {
      analogWrite(6, value);
    }
  }
}

void blinkLED(int pin)
{
  for (int i = 0; i < 3; i++)
  {
    digitalWrite(pin, HIGH);
    delay(500);
    digitalWrite(pin, LOW);
    delay(500);
  }
}
```


## Author

Karlo Rešetar [@karloresetar](https://github.com/karloresetar)
