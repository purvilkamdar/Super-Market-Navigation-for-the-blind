package com.example.parthpachchigar.hardwaredevice;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by parth.pachchigar on 10/29/17.
 */

public class Localization {
    private double knownLat;
    private double knownLong;
    private static double curLat;
    private static double curLong;
    private static boolean setCurrent=false;
    int credentialId;
    Mqtt_Publish_subscribe mqtt_publish_subscribe;

    public Localization(Context mContext) throws IOException{
        credentialId=mContext.getResources().getIdentifier("credentials", "raw", mContext.getPackageName());
        mqtt_publish_subscribe=new Mqtt_Publish_subscribe(mContext, "supermarketnavigation", "supermarketnavigation", "coordinates", credentialId);
        //Cahil
        knownLat=37.331434;
        knownLong=-121.905350;

        //MLK
        //knownLat=37.3354626;
        //knownLong=-121.8850214;
        if(!setCurrent) {
            curLat = knownLat;
            curLong = knownLong;
            setCurrent=true;
        }
    }
    public void calculateCord(int theta,double R){
        double dx = R*Math.sin(theta); //theta measured clockwise from due north
        double dy = R*Math.cos(theta); //dx, dy same units as R
        double delta_longitude = dx/(111320*Math.cos(curLat)); //dx, dy in meters
        double delta_latitude = dy/110540; //result in degrees long/lat
        curLat=curLat+delta_latitude;
        curLong=curLong+delta_longitude;
        Log.i("Data:",String.format("%d %.6f %.6f",theta,curLat,curLong));
        sendCord(curLat,curLong);
    }
    public void sendCord(double lat,double lon){
        mqtt_publish_subscribe.execute(String.format("%.6f",lat), String.format("%.6f",lon));
    }
}
