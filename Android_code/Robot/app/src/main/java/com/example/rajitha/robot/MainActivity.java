package com.example.rajitha.robot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    WebView webView;
    TextView text;
    EditText editText;
    App sever;
    SeekBar seekBar;
    Switch aSwitch;
    TextView text_f_b;
    TextView text_speed;
    TextView text_rotation;
    TextView text_status;
    TextView text_accessPoint;
    EditText input_mac;
    ConstraintLayout layout;
    ConstraintLayout layout_close;
    ConstraintLayout layout_help;
    ConstraintLayout layout_wait;
    boolean executed=false;

    int forword_backword=0;
    boolean forword_back=false;
    private SensorManager mSensorManager;
    private Sensor mRotational;
    ApManager apManager=new ApManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent=new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        setContentView(R.layout.activity_main);



        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //webView=(WebView) findViewById(R.id.webView);
        text =(TextView) findViewById(R.id.textView);
        text_f_b =(TextView) findViewById(R.id.textView2);
        text_speed =(TextView) findViewById(R.id.textView3);
        text_rotation =(TextView) findViewById(R.id.textView6);
        text_status =(TextView) findViewById(R.id.textView7);
        text_accessPoint =(TextView) findViewById(R.id.textView8);
        input_mac=(EditText) findViewById(R.id.editText);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotational = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //layout =(ConstraintLayout) findViewById(R.id.constraint);
        layout =(ConstraintLayout) findViewById(R.id.constraint);
        layout_close =(ConstraintLayout) findViewById(R.id.layout_close);
        layout_help =(ConstraintLayout) findViewById(R.id.layout_help);
        layout_wait =(ConstraintLayout) findViewById(R.id.layout_wait);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        aSwitch=(Switch) findViewById(R.id.switch1);

        text_speed.setText("Speed     : 0");
        text_accessPoint.setText("Access point : Not set");
        text_status.setText("Status     : Not Connect");

        //text.setText(text.getText().toString()+"\n"+"--Start--");

        //webView.loadUrl("http://localhost:8080/");

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    text_f_b.setText("Backword");
                    forword_back=true;
                }else{
                    text_f_b.setText("Forword");
                    forword_back=false;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                forword_backword=progress;
                text_speed.setText("Speed     : "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    //back button
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        if (!isPermited()){
            getPermission();
        }else{
            if (!executed) {
                executed=true;
                try {
                    sever = new App();
                } catch (IOException e) {
                    e.printStackTrace();
                    //text.setText(text.getText().toString()+"\n"+"Error : "+e.getMessage().toString());
                }

                apManager.getDetail(MainActivity.this);

                //apManager.configureSSID(MainActivity.this);

                final Dialog dialog=new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_wait);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (apManager.issetSSID(MainActivity.this)){
                            text_accessPoint.setText("Access point : Established");
                        }else{
                            text_accessPoint.setText("Access point : Error");
                        }
                    }
                },4000);
                connected_mac=false;
                check_connected();
            }else{
                connected_mac=false;
                check_connected();
            }

        }
        mSensorManager.registerListener(this,mRotational,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isPermited()){
            onChangeSensor(event);
        }
    }

    public void onChangeSensor(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            Float sense2=event.values[1];
            Float sense3=event.values[2];
            int val1= (int) (sense2/1);
            int val2= (int) (sense3/1);

            text.setText(String.valueOf(val2));
            if (val2 >0){
                layout.setRotation(0);

                if (val2<15){
                    val2=15;
                }
                int rotation_val=val1*(130/val2);

                if (val1 >0){
                    //turn left
                    //text_rotation.setText("Rotation : Left  ->"+val1*2);
                    text_rotation.setText("Rotation : Left  ->"+rotation_val);
                    String direction="22";
                    if (forword_back){
                        direction="33";
                    }
                    if (forword_backword<10){
                        direction="00";
                    }
                    sever.command(direction, forword_backword,rotation_val);

                }else{
                    //turn right
                    //text_rotation.setText("Rotation : Right ->"+val1*-2);
                    text_rotation.setText("Rotation : Right ->"+rotation_val*-1);
                    String direction="11";
                    if (forword_back){
                        direction="44";
                    }
                    if (forword_backword<10){
                        direction="00";
                    }
                    sever.command(direction, forword_backword,rotation_val*-1);

                }
                //webView.loadUrl("http://localhost:8080/");
            }else{
                layout.setRotation(180);

                if (val2>-15){
                    val2=-15;
                }
                int rotation_val=val1*(-130/val2);


                if (val1 >0){
                    //turn right
                    //text_rotation.setText("Rotation : Right  ->"+val1*2);
                    text_rotation.setText("Rotation : Right  ->"+rotation_val);
                    String direction="11";
                    if (forword_back){
                        direction="44";
                    }
                    if (forword_backword<10){
                        direction="00";
                    }
                    sever.command(direction, forword_backword,rotation_val);

                }else{
                    //turn left
                    //text_rotation.setText("Rotation : Left ->"+val1*-2);
                    text_rotation.setText("Rotation : Left ->"+rotation_val*-1);
                    String direction="22";
                    if (forword_back){
                        direction="33";
                    }
                    if (forword_backword<10){
                        direction="00";
                    }
                    sever.command(direction, forword_backword,rotation_val*-1);

                }
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void refresh(View view) {
        //webView.loadUrl("http://localhost:8080/");
    }


    public void getPermission(){
        ToneGenerator toneGenerator=new ToneGenerator(AudioManager.STREAM_MUSIC,100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP,1500);
        new AlertDialog.Builder(this).setTitle("About Permission...").setMessage("You have to give permission for 'Allow write system settings' manually.Click 'Yes' to open permission window.Click 'No' to exit.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false).show();
    }
    public boolean isPermited(){
        boolean _permited = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                _permited=false;
            }else{
                _permited=true;
            }
        }
        return _permited;
    }

    public void close_activity(View view) {
        apManager.resetSSID(MainActivity.this);

        Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_close);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },4000);
    }

    public void help_activity(View view) {
        Dialog dialog=new Dialog(this);
        dialog.setTitle("Help...");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_help);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        /*new AlertDialog.Builder(this).setTitle("Help...").setMessage("Make sure use close button to exit from application.Otherwise lost your hotspot settings...").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();*/
    }

    public void set_mac(View view){
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    String mac_address="a0:20:a6:02:97:72";
    public boolean is_connected(String mac_address){
        ArrayList<String> mac = apManager.getConnectedDevicesMac();
        boolean connected=false;
        for (int i=0;i<mac.size();i++){
            String each_mac=mac.get(i);
            if (each_mac.equals(mac_address)){
                connected=true;
            }
        }
        return connected;
    }

    public void set_mac_input(View view){
        String mac_add=editText.getText().toString();
        if (mac_add.length()!=17){

        }
    }

    boolean connected_mac=false;
    public void check_connected(){
        Handler handler1=new Handler();
        if (!connected_mac){
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (is_connected(mac_address)){
                        text_status.setText("Status     : Connected");
                        connected_mac=true;
                    }else{
                        check_connected();
                        text_status.setText("Status     : Not Connect");
                    }
                }
            },1000);
        }
    }
}


//now access web page using http://192.168.43.1:8080/