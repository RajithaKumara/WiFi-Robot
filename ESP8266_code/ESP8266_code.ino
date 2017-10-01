/**
 * Edited Version of
 * 
 * ESP8266HTTPClient
 * 
 * BasicHTTPClient.ino
 *
 *  Created on: 24.05.2015
 *
 */



#include <Arduino.h>

#include <ESP8266WiFi.h>

#include <ESP8266WiFiMulti.h>

#include <ESP8266HTTPClient.h>

#define USE_SERIAL Serial

ESP8266WiFiMulti WiFiMulti;

void setup() {
    pinMode(LED_BUILTIN, OUTPUT);
    USE_SERIAL.begin(115200);

    for(uint8_t t = 4; t > 0; t--) {  // This loop not necessary.
        digitalWrite(LED_BUILTIN, HIGH);
        USE_SERIAL.flush();
        delay(1000);
        digitalWrite(LED_BUILTIN, LOW);
    }

    WiFiMulti.addAP("Robot", "robo@123"); //conect to access point if exist which SSID=Robot and password=robo@123 

}

void loop() {
    if(WiFiMulti.run() == WL_CONNECTED) { //if connection is already setting up then request server updates

        HTTPClient http;
        // configure traged server and url
        //http.begin("https://192.168.43.1:8080/", "7a 9c f4 db 40 d3 62 5a 6e 21 bc 5c cc 66 c8 3e a1 45 59 38"); //HTTPS
        http.begin("http://192.168.43.1:8080/"); //HTTP
        
        // start connection and send HTTP header
        int httpCode = http.GET();

        // httpCode will be negative on error
        if(httpCode > 0) {
            // file found at server
            if(httpCode == HTTP_CODE_OK) {
                String payload = http.getString();
                USE_SERIAL.println(payload);
            }
        } else {
            //do if error getting httpCode
            USE_SERIAL.println("#00#000#000#");
        }

        http.end();
    }
    else{
      //still not connected to access point, try to connect
      WiFiMulti.addAP("Robot", "robo@123");
      //do if not exist access point which try to connect
      USE_SERIAL.println("#00#000#000#");
    }
    delay(10);
}

