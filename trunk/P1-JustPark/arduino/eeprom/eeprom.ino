#include <EEPROM.h>

int space = 100;

void setup(){
  EEPROM.write(0, space);  
}

void loop(){
}
