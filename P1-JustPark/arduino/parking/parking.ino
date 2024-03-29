#include <EEPROM.h>
#include <SPI.h>
#include <HttpClient.h>
#include <WiFi.h>
#include <Servo.h> 

char ssid[] = "HasDroid";
char pass[] = "compubi1";

const char kHostname[] = "json.internetdelascosas.es";
const char path_prefix[] = "/arduino/add.php?device_id=200&data_name=parking&data_value=";
char kPath[200] = "";
const int kNetworkTimeout = 10*1000;
const int kNetworkDelay = 1000;

int places;
Servo servo;
int buttonState = 0;
int motionState = 0;
int inout = -1;

int inputButtonIn = A1;
int inputButtonOut = A2;
int led = A3;
int ledNot = A4;
int motionDetect = A5;

int sleepPin = 2;
int speakerPin = 6;
int servoPin = 9;

int length = 15; // the number of notes
char notes[] = "cc "; // a space represents a rest
int beats[] = { 1, 2, 4 };
int tempo = 300;

void setup(){
  Serial.begin(9600);
  readEEPROM();
  servo.attach(servoPin);
  digitalWrite(sleepPin, HIGH);
  
  pinMode(inputButtonIn, INPUT);
  pinMode(inputButtonOut, INPUT);
  pinMode(motionDetect, INPUT);
  
  pinMode(led, OUTPUT);
  pinMode(sleepPin, OUTPUT);
  pinMode(servoPin, OUTPUT);
  pinMode(speakerPin, OUTPUT);
  
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present"); 
    // don't continue:
    while(true);
  } 
}

void loop(){
  inout = -1;
  
  if(places<1)
    digitalWrite(ledNot, HIGH);  
  else  
    digitalWrite(ledNot, LOW);
  
  while(inout == -1){
    Serial.println("Waiting..."); 
    if(places > 0)
      inout = detectCar();
    if(inout == -1)
      inout = buttonOut();
  }
  
  if(inout == 0){
    //sound();
    buttonIn();    
    moveServo();  
    places = places - 1;
  }else{    
    Serial.println("Led");
    digitalWrite(led, HIGH);
    delay(3000);
    digitalWrite(led, LOW);    
    places = places + 1;
  }  
  
  writeEEPROM(places);
  
  if( WiFi.status() != WL_CONNECTED){
    connectToWifi();
  }
  if(WiFi.status() == WL_CONNECTED){
    sendData(places);
  }
}

void readEEPROM(){
  //Save values in EEPROM
  places = EEPROM.read(0);  
  Serial.println(places);  
}

int detectCar(){  
  Serial.println("Detect car");
  //do{    
    motionState = digitalRead(motionDetect);
  //}while(motionState == HIGH);
  
  if(motionState == HIGH) 
    return -1;
  else
    return 0;
}

void writeEEPROM(int p){
   EEPROM.write(0, p);
}

void connectToWifi(){
  int status = WiFi.status();
  // attempt to connect to Wifi network:
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:    
    status = WiFi.begin(ssid);
 
  // you're connected now, so print out the status:
  printWifiStatus();
}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

void sendData(int data){
  int err =0;
  
  WiFiClient c;
  HttpClient http(c);
  
  sprintf(kPath,"%s%d",path_prefix,data);
  Serial.print("Requesting URL: ");
  Serial.println(kPath);
  
  err = http.get(kHostname, kPath);
  if (err == 0)
  {
    Serial.println("startedRequest ok");

    err = http.responseStatusCode();
    if (err >= 0)
    {
      Serial.print("Got status code: ");
      Serial.println(err);

      // Usually you'd check that the response code is 200 or a
      // similar "success" code (200-299) before carrying on,
      // but we'll print out whatever response we get

      err = http.skipResponseHeaders();
      if (err >= 0)
      {
        int bodyLen = http.contentLength();
        Serial.print("Content length is: ");
        Serial.println(bodyLen);
        Serial.println();
        Serial.println("Body returned follows:");
      
        // Now we've got to the body, so we can print it out
        unsigned long timeoutStart = millis();
        char c;
        // Whilst we haven't timed out & haven't reached the end of the body
        while ( (http.connected() || http.available()) &&
               ((millis() - timeoutStart) < kNetworkTimeout) )
        {
            if (http.available())
            {
                c = http.read();
                // Print out this character
                Serial.print(c);
               
                bodyLen--;
                // We read something, reset the timeout counter
                timeoutStart = millis();
            }
            else
            {
                // We haven't got any data, so let's pause to allow some to
                // arrive
                delay(kNetworkDelay);
            }
        }
      }
      else
      {
        Serial.print("Failed to skip response headers: ");
        Serial.println(err);
      }
    }
    else
    {    
      Serial.print("Getting response failed: ");
      Serial.println(err);
    }
  }
  else
  {
    Serial.print("Connect failed: ");
    Serial.println(err);
  }
  http.stop(); 
  
}

void sound(){
  for (int i = 0; i < length; i++) {
    if (notes[i] == ' ') {
      delay(beats[i] * tempo); // rest
    } else {
      playNote(notes[i], beats[i] * tempo);
    }
    
    // pause between notes
    delay(tempo / 2); 
  }
}

void playTone(int tone, int duration) {
  for (long i = 0; i < duration * 1000L; i += tone * 2) {
    digitalWrite(speakerPin, HIGH);
    delayMicroseconds(tone);
    digitalWrite(speakerPin, LOW);
    delayMicroseconds(tone);
  }
}

void playNote(char note, int duration) {
  char names[] = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C' };
  int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 };
  
  // play the tone corresponding to the note name
  for (int i = 0; i < 8; i++) {
    if (names[i] == note) {
      playTone(tones[i], duration);
    }
  }
}

void moveServo(){
  Serial.println("Move");
  servo.write(90);
  delay(2000);
  servo.write(0);
}

int buttonOut(){
  Serial.println("Button Out");
  int i = 0;
  do{
    buttonState = digitalRead(inputButtonOut); 
    i++;   
  }while(buttonState == HIGH && i<10);
  if(buttonState == HIGH)
    return -1;
  else
    return 1;
}

void buttonIn(){  
  Serial.println("Button In");
  do{
  buttonState = digitalRead(inputButtonIn); 
  }while(buttonState == HIGH);
}
