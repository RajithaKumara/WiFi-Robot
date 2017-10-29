package com.example.rajitha.robot;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;

import cc.mvdan.accesspoint.WifiApControl;

/**
 * Created by rajitha on 7/25/17.
 */

public class ApManager {

    public ApManager(){

    }
    //check whether wifi hotspot on or off
    public boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {

            Method setConfigMethod =wifimanager.getClass().getMethod("setWifiApConfiguration",WifiConfiguration.class);
            setConfigMethod.invoke(wifimanager,wificonfiguration);
            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void configureSSID(Context context){
        WifiConfiguration wifiConfiguration=new WifiConfiguration();

        wifiConfiguration.SSID="Robot";
        wifiConfiguration.preSharedKey="robo@123";
        wifiConfiguration.hiddenSSID=false;
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedProtocols.set(Protocol.RSN);
        wifiConfiguration.allowedKeyManagement.set(4);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

        WifiApControl apControl=WifiApControl.getInstance(context);
        apControl.setEnabled(wifiConfiguration,true);
    }

    public boolean issetSSID(Context context){
        boolean isset=false;
        WifiApControl apControl=WifiApControl.getInstance(context);
        String ssid=apControl.getConfiguration().SSID;
        String pass=apControl.getConfiguration().preSharedKey;
        if ((ssid.equals("Robot")) && (pass.equals("robo@123"))){
            isset=true;
        }
        return isset;
    }


    private String old_ssid="default";
    private String old_preSheredKey=null;
    private boolean old_hiddenSSID=false;
    private BitSet old_allowedAuthAlgorithms=null;
    private BitSet old_allowedProtocols=null;
    private BitSet old_allowedKeyManagement=null;
    private BitSet old_allowedPairwiseCiphers=null;
    private BitSet old_allowedGroupCiphers=null;


    public void getDetail(Context context){
        WifiApControl apControl=WifiApControl.getInstance(context);

        old_ssid=apControl.getConfiguration().SSID;
        old_preSheredKey=apControl.getConfiguration().preSharedKey;
        old_hiddenSSID=apControl.getConfiguration().hiddenSSID;
        old_allowedAuthAlgorithms=apControl.getConfiguration().allowedAuthAlgorithms;
        old_allowedProtocols=apControl.getConfiguration().allowedProtocols;
        old_allowedKeyManagement=apControl.getConfiguration().allowedKeyManagement;
        old_allowedPairwiseCiphers=apControl.getConfiguration().allowedPairwiseCiphers;
        old_allowedGroupCiphers=apControl.getConfiguration().allowedGroupCiphers;

    }
    public void resetSSID(Context context){
        WifiConfiguration wifiConfiguration=new WifiConfiguration();

        wifiConfiguration.SSID=old_ssid;
        wifiConfiguration.preSharedKey=old_preSheredKey;
        wifiConfiguration.hiddenSSID=old_hiddenSSID;

        int val1=bitsetToInt(old_allowedAuthAlgorithms);
        if (val1!=-1) {
            wifiConfiguration.allowedAuthAlgorithms.set(val1);
        }

        int val2=bitsetToInt(old_allowedProtocols);
        if (val2!=-1) {
            wifiConfiguration.allowedProtocols.set(val2);
        }

        int val3=bitsetToInt(old_allowedKeyManagement);
        if (val3!=-1) {
            wifiConfiguration.allowedKeyManagement.set(val3);
        }

        int val4=bitsetToInt(old_allowedPairwiseCiphers);
        if (val4!=-1) {
            wifiConfiguration.allowedPairwiseCiphers.set(val4);
        }

        int val5=bitsetToInt(old_allowedGroupCiphers);
        if (val5!=-1) {
            wifiConfiguration.allowedGroupCiphers.set(val5);
        }

        final WifiApControl apControl=WifiApControl.getInstance(context);
        apControl.setEnabled(wifiConfiguration,true);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apControl.setEnabled(null,false);
            }
        }, 2000);
    }

    public int bitsetToInt(BitSet bitSet){
        String s_bitset=bitSet.toString();
        char a_char=s_bitset.charAt(1);
        int result=-1;
        try {
            result = Character.getNumericValue(a_char);
            return result;
        }catch (Exception e){
            result=-1;
        }
        return result;
    }

    public ArrayList<String> getConnectedDevicesMac()
    {
        ArrayList<String> res = new ArrayList<String>();
        //NetManager.updateArpFile();

        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null)
            {
                String[] sp = line.split(" +");
                if (sp[3].matches("..:..:..:..:..:.."))
                    res.add(sp[3]);
            }

            br.close();
        }
        catch (Exception e)
        {}

        return res;
    }

}
