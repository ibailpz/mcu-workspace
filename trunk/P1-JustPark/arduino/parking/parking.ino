#include <EEPROM.h>
#include <SPI.h>
#include <HttpClient.h>
#include <WiFi.h>

char ssid[] = "HasDroid";
char pass[] = "compubi1";

const char kHostname[] = "json.internetdelascosas.es";
const char path_prefix_1[] = "/arduino/add.php?device_id=3&data_name=temp&data_value=";
const char path_prefix_0[] = "/arduino/add.php?device_id=3&data_name=light&data_value=";
char kPath[200] = "";
const int kNetworkTimeout = 10*1000;
const int kNetworkDelay = 1000;

int places;

int sensor = A0;
int inputButton = A1;
int led = A2;
int speakerPin = 6;

void setup(){
  Serial.begin(9600);
  readEEPROM();
  
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present"); 
    // don't continue:
    while(true);
  } 
}

void loop(){
  
}

void readEEPROM(){
  //Save values in EEPROM
  places = EEPROM.read(0);  
  Serial.println(places);  
}
