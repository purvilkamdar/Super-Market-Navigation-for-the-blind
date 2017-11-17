package com.example.parthpachchigar.hardwaredevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by parth.pachchigar on 10/29/17.
 */

public class Wifi extends Thread{
    private HashMap<String,String> WifiList;
    private boolean wifi_flag;
    private WifiManager wifiManager;
    private List<ScanResult> ls;
    private HashMap<String, String> resultlist;
    private Context context;
    private Activity activity;
    public Wifi(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }
    @Override
    public void run()
    {

        wifi_flag=false;
        //String ssid="Gujarat";
        //String password="4206pppancaks";
        Log.w("Wifi", "Inside Wifi Connect");
        try {

            WifiList = new HashMap<String, String>();
            WifiList.put("pup","parth1994");
            WifiList.put("Gujarat", "4206pppancaks");
            WifiList.put("Purvil", "1234567890");
            WifiList.put("SJSU_premier", "$Purvil92");

            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            resultlist = new HashMap<String, String>();
            //Log.w("Intermdeiate:","::");
            this.activity.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {

                        Log.w("Wifi Scan:", "Starting Wifi Scan");
                        ls = wifiManager.getScanResults();

                        Log.w("WIFI Scan:", "Scan Results size:" + ls.size());

                        for (ScanResult result : ls) {
                            //Log.w("Wifi Result:", ls.get(i).toString());
                            resultlist.put(result.SSID, result.capabilities);
                        }

                        //wifi_flag=true;
                        if(!wifi_flag)
                            connect_to_Wifi(resultlist);
                        //if(resultlist.containsKey("Gujarat"))
                        //Log.w("Wifi Scan:","Gujarat:"+resultlist.get("Gujarat"));
                    } catch (Exception e) {
                        Log.w("Exception in Wifi Scan:", e.getMessage().toString());
                    }
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            //connect_to_Wifi(resultlist);
        } catch (Exception e) {
            Log.w("WIFI", e.getMessage().toString());
        }
    }



    public void connect_to_Wifi(HashMap<String,String>resultlist)
    {
        wifi_flag=true;
        try
        {
            String ssid = "";
            String password = "";
            Log.i("List",resultlist.toString());
              if (resultlist.containsKey("Gujarat")) {
                ssid = "Gujarat";
                password = WifiList.get("Gujarat");


            }else if (resultlist.containsKey("pup")) {
                ssid = "pup";
                password = WifiList.get("pup");
            }else if(resultlist.containsKey("Gujarat-5"))
            {
                ssid="Gujarat-5";
                password=WifiList.get("Gujarat");

            }
            else
            {
                Log.w("Wifi:","Result list did not find any known Wifi Networks");
            }
            Log.w("Wifi:","Connected to SSID:"+ssid+"Password:"+password);

            String args = "am startservice  --user 0 -n com.google.wifisetup/.WifiSetupService -a WifiSetupService.Connect -e ssid " + ssid + " -e passphrase " + password + "";

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(args);

            //wifiReceiver = new WifiReceiver();


            InputStream is = process.getErrorStream();

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {

                Log.i("WIFI:", "" + line);
            }

            Thread.sleep(1000);
            WifiInfo wi = (WifiInfo) wifiManager.getConnectionInfo();
            if (wi.getNetworkId() == -1) {
                wifiManager.setWifiEnabled(false);
            }
            //Thread.sleep(300*1000);

        }
        catch(Exception e) {
            Log.w("WIFI error:",e);
        }
    }



}