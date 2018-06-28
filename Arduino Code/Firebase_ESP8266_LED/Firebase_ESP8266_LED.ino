#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <ESP8266WiFiMulti.h>

#define FIREBASE_HOST "sassistbot.firebaseio.com"
#define FIREBASE_AUTH "iR9d20AxuvbbayVziXgY0VZnLBCRstbnkSOJz8bs"
#define LED_P2 2
#define DEBUG_PRINT 1

char wifi_ssid[]       = "WwjVic";
char wifi_password[]   = "1234567890Wei";

ESP8266WiFiMulti WiFiMulti;

void waitForWifi() {
  while (WiFiMulti.run() != WL_CONNECTED) {
    delay(100);
    if (DEBUG_PRINT) {
      Serial.println("waiting for wifi");
    }
  }
  if (DEBUG_PRINT) {
    Serial.println("\nconnected to network " + String(wifi_ssid) + "\n");
  }
}

void setup() {
  delay(5000);
  if (DEBUG_PRINT) {
    Serial.println("START!");
  }
  pinMode(LED_P2, OUTPUT);

  digitalWrite(LED_P2, LOW);
  Serial.begin(9600);

  WiFiMulti.addAP(wifi_ssid, wifi_password);
  waitForWifi();

  if (DEBUG_PRINT) Serial.println(WiFi.localIP());
  delay(5000);
  if (DEBUG_PRINT) {
    Serial.println("Going to connect Firebase! : ");
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.setInt("LED_01", 0);
//  while (Firebase.success() ) {
//    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
//    Firebase.setInt("LED_01", 0);
    if (DEBUG_PRINT) {
      Serial.println("Retrying "+Firebase.success() );
    }
//  }
}

int LED_STATE = 0;

void loop() {
  LED_STATE = Firebase.getInt("LED_01");
  if (DEBUG_PRINT) {
    Serial.print("LED_01: ");
    Serial.println(LED_STATE);
  }

  if (LED_STATE == 1) {
    digitalWrite(LED_P2, HIGH);
  }
  else digitalWrite(LED_P2, LOW);

  delay(1000);
}
