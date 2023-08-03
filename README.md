# BluetoothAndroidArduinoLEDController
Završni rad

Arduino Code:
```
int data;

void setup()
{
  Serial.begin(9600);
  pinMode(6, OUTPUT);
}

void loop()
{
  while (Serial.available() > 0)
  {
    data = Serial.read();

    if (data == 'x')
    {
      digitalWrite(6, HIGH);
    }
    else if (data == 'y')
    {
      digitalWrite(6, LOW);
    }
    else if (data == 'z')
    {
      blinkLED(6);
    }
    else if (data >= 0 && data <= 100)
    {
      int brightness = map(data, 0, 100, 0, 255);
      analogWrite(6, brightness);
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

