#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11); // RX, TX
                                 //pin 10 set to receive serial data from ESP8266
                                 //pin 11 set to transmit serial data to ESP8266

int A=9; //right motor rotate backword
int B=6; //right motor rotate forword
int C=5; //left motor rotate backword
int D=3; //left motor rotate forword
String inString = ""; //initiate string variable for collect serial characters
int tolerance=35;     //value can be differ
int minimmum_speed=20;
int maximum_speed=100;
int maximum_rotation=100;

void setup() {
  // put your setup code here, to run once:
  pinMode(A,OUTPUT);
  pinMode(B,OUTPUT);
  pinMode(C,OUTPUT);
  pinMode(D,OUTPUT);
  Serial.begin(115200);
  while (!Serial) {
    Serial.print("."); // wait for serial port to connect. Needed for native USB port only
  }
  Serial.println("Ready....");
  mySerial.begin(115200); //initiate SoftwareSerial for read Serial data comming from  ESP8266 module
}

void loop() {
  if (mySerial.available()) {// if receive data from ESP8266
    
    /**
     * Receiving code structure
     * #00#000#000# 
     * '#' character use to divide data stream to three parts
     * first two integer values use for check direction. use two digits for error detection
     * next three digits use to get speed value
     * next three digits use to get rotation value
     * #00#000#000# => means stop all robot movements
     * #11#xxx#xxx# => forword right
     * #22#xxx#xxx# => forword left
     * #33#xxx#xxx# => backword left
     * #44#xxx#xxx# => backword right
     * turning or go stright decide by tolerance value
     * example: #11#100#040# => if 40>tolerance then turn right(forword), if 40<tolerance then go ahead
     */
     
    inString = ""; //clear variable before collect next data set
    while (mySerial.available() > 0) {
      int inChar = mySerial.read(); //read receiving characters one by one    
      if (isDigit(inChar)) { //if the character is digit, collect receiving characters to string
        // convert the incoming byte to a char
        // and add it to the string:
        inString += (char)inChar;
        
      }
      else if (inChar == '\n') {         //getting '\n' character means finish one data line from ESP8266
        Serial.println(inString);   //print collected string on serial monitor
        if (inString.length()==8){
          String dir1=inString.substring(0,1); //get direction
          String dir2=inString.substring(1,2);
          String forword_backword=inString.substring(2,5); // get speed value
          String left_right=inString.substring(5,8);// get rotation value
          int f_b=forword_backword.toInt(); //speed value convert into integer 
          int l_r=left_right.toInt(); //rotation value convert into integer 


          if (f_b>maximum_speed){ //speed  limitation forword, backword
            f_b=maximum_speed;
          }
          if (l_r>maximum_rotation){ //speed limitation for turning
            l_r=maximum_rotation;
          }

          
          if ((f_b<minimmum_speed) || ((dir1=="0") && (dir2=="0"))){
            hold();
          }
          else if ((dir1 == "1") && (dir2=="1")) {
            //forword right
            if (l_r<tolerance){
              goAhead(f_b);
              //Serial.println("goAhead");
            }
            else{
              turnRightF(l_r);
              //Serial.println("turn right");
            }
          }
          else if ((dir1 == "2") && (dir2=="2")) {
            //forword left
            if (l_r<tolerance){
              goAhead(f_b);
              //Serial.println("goAhead");
            }
            else{
              turnLeftF(l_r);
              //Serial.println("turn left");
            }
          }
          else if ((dir1 == "3") && (dir2=="3")) {
            //backword left
            if (l_r<tolerance){
              goRev(f_b);
              //Serial.println("goAhead");
            }
            else{
              turnLeftB(l_r);
              //Serial.println("turn left");
            }
          }
          else if ((dir1 == "4") && (dir2=="4")) {
            //backword right
            if (l_r<tolerance){
              goRev(f_b);
              //Serial.println("goAhead");
            }
            else{
              turnRightB(l_r);
              //Serial.println("turn left");
            }
          }
        }
      }
    }
  }
}

void hold(){ //stop all movement
  analogWrite(A,0);
  analogWrite(B,0);
  analogWrite(C,0);
  analogWrite(D,0);
}
void turnRightF(int val){ //turn right when going forword
  analogWrite(A,0);
  analogWrite(C,0);
  analogWrite(D,val);
  analogWrite(B,val/3);
}
void turnLeftF(int val){ //turn left when going forword
  analogWrite(A,0);
  analogWrite(C,0);
  analogWrite(D,val/3);
  analogWrite(B,val);
}
void turnRightB(int val){ //turn right when going backword
  analogWrite(D,0);
  analogWrite(B,0);
  analogWrite(A,val/3);
  analogWrite(C,val);
}
void turnLeftB(int val){ //turn left when going backword
  analogWrite(D,0);
  analogWrite(B,0);
  analogWrite(A,val);
  analogWrite(C,val/3);
}
void goAhead(int val){ //no turn, go ahead
  analogWrite(A,0);
  analogWrite(C,0);
  analogWrite(D,val);
  analogWrite(B,val);
}
void goRev(int val){ //no turn, go reverse
  analogWrite(D,0);
  analogWrite(B,0);
  analogWrite(A,val);
  analogWrite(C,val);
}

