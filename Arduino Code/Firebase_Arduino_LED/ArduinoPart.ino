#include <SoftwareSerial.h>
#define rxPin 0
#define txPin 1
#define LEDP 13
SoftwareSerial esp8266(rxPin, txPin);

String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete
String commandString = "";

void setup() {
  pinMode(LEDP, OUTPUT);
  digitalWrite(LEDP, LOW);

  // put your setup code here, to run once:
  Serial.begin(9600);
}
void loop() {
  // put your main code here, to run repeatedly:
  if (stringComplete == true) {
    //Serial.print("Printing : ");
    Serial.println(inputString);
    // String x = inputString.substring(8,9);
    if (inputString.length() > 8) {
      if (inputString[8] == '1') {
        digitalWrite(LEDP, HIGH);
      }
      else {
        digitalWrite(LEDP, LOW);
      }
    }


    inputString = "";
    stringComplete = false;
  }

}

void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    if (inChar == '\n') {
      stringComplete = true;
    }
    else {
      // add it to the inputString:
      inputString += inChar;
      // if the incoming character is a newline, set a flag
      // so the main loop can do something about it:
    }
  }
}
