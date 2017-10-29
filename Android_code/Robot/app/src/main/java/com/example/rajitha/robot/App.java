package com.example.rajitha.robot;

import android.support.v4.widget.TextViewCompat;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import android.widget.TextView;

/**
 * Created by rajitha on 7/24/17.
 */

public class App extends NanoHTTPD {

    String msg="";

    public App() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void command(String direction,int forword_backword,int left_right){
        String f_b=String.valueOf(forword_backword);
        String l_r=String.valueOf(left_right);
        if (forword_backword<100){
            f_b="0"+String.valueOf(forword_backword);
        }
        if (left_right<100){
            l_r="0"+String.valueOf(left_right);
        }
        if (forword_backword<10){
            f_b="00"+String.valueOf(forword_backword);
        }
        if (left_right<10){
            l_r="00"+String.valueOf(left_right);
        }

        msg="#"+direction.toString()+"#"+f_b+"#"+l_r+"#";
    }

    public String getMsg(){
        return msg;
    }

    @Override
    public Response serve(NanoHTTPD.IHTTPSession session) {

        return newFixedLengthResponse(msg);
    }
}
