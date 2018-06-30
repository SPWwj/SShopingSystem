#include <Servo.h>


Servo svL;
Servo svR;

String bluetoothRead, Str_x, Str_y, Str_p;
int x ;
int y ;
int points;
int length;
const int TIMEUP=250;
int timeCount=0;



void setup() {
  Serial.begin(9600);

  svL.attach(9);
  svR.attach(10);


}

void loop() {
  
//Serial.println("I am still running");
  int i = 0;
  char commandbuffer[20];


  if (Serial.available()) {
    delay(10);

    while ( Serial.available() && i < 19) {
      commandbuffer[i++] = Serial.read();


    }
    commandbuffer[i++] = '\0';
    bluetoothRead = (char*)commandbuffer;
    length = bluetoothRead.length();
    Serial.println(commandbuffer);


    if (bluetoothRead.substring(0, 1).equals("x")) {

      int i = 1;
      while (bluetoothRead.substring(i, i + 1) != ("y")) {
        i++;
      }

      Str_x = bluetoothRead.substring(1, i);
      x = Str_x.toInt();



      Str_y = bluetoothRead.substring(i + 1, length - 1);
      y = Str_y.toInt();

      Str_p = bluetoothRead.substring(length - 1, length);
      points = Str_p.toInt();

      i = 1;


      Stop();

      if (x < 40) {
        Left();

      }
      if (x > 140) {
        Right();

      }
      if (x < 140 && x > 40) {
        if (points == 1) {
          Forward();
        }
        if (points == 0) {
          Stop();
        }
        if (points == 2) {
          Back();
        }
      }



    }
    timeCount=0;
  }
  else {
    timeCount++;
    if(timeCount>TIMEUP) {
      Stop();
      timeCount=0;
    }
  }
 Serial.println(timeCount);
}

void Left() {
  svL.write(93);
  svR.write(53);

}


void Right() {

  svL.write(133);
  svR.write(93);

}


void Forward() {
  svL.write(113);
  svR.write(23);
  //print(""forward)
}


void Back() {
  svL.write(23);
  svR.write(113);
}


void Stop() {
  svL.write(93);
  svR.write(93);
}
