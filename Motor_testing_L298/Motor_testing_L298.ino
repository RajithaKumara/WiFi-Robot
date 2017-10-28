/* ##Declare IN pins.##
 *  
 * 3,5,6 and 9 are pins of Arduino board. 
 * You can change integer value of this pins. 
 * But make sure that pins are PWM pins.
 * PWM pins are altered one by one.
 * Use www.arduino.cc to more details about PWM pins.
 */
int IN1=9;
int IN2=6;
int IN3=5;
int IN4=3;

/* ##Declare speed of motors.##
 *  
 * This value in range 0 and 255. 
 * Sometimes, at small values motor not working. 
 * Because motor not satisfied current supplied by motor contoller.
 */
int value_of_speed=100;

void setup() {
  //initiaize IN pins as output
  pinMode(IN1,OUTPUT);
  pinMode(IN2,OUTPUT);
  pinMode(IN3,OUTPUT);
  pinMode(IN4,OUTPUT);
}

void loop() {
  //check IN1 pin
  analogWrite(IN1,value_of_speed);
  analogWrite(IN2,0);
  analogWrite(IN3,0);
  analogWrite(IN4,0);

  delay(2000); //delay 2 seconds

  //check IN2 pin
  analogWrite(IN1,0);
  analogWrite(IN2,value_of_speed);
  analogWrite(IN3,0);
  analogWrite(IN4,0);

  delay(2000);

  //check IN3 pin
  analogWrite(IN1,0);
  analogWrite(IN2,0);
  analogWrite(IN3,value_of_speed);
  analogWrite(IN4,0);

  delay(2000);

  //check IN4 pin
  analogWrite(IN1,0);
  analogWrite(IN2,0);
  analogWrite(IN3,0);
  analogWrite(IN4,value_of_speed);

  delay(2000);
}

