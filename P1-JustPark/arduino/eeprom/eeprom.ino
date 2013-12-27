#include <EEPROM.h>

int space = 72;

void setup(){
  EEPROM.write(0, space);  
}

void loop(){
}
