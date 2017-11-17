package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import java.io.IOException;

/**
 * Created by parth.pachchigar on 10/27/17.
 */

public class Sensing extends Thread {
    private static Compass compass;
    public Sensing(){
        try{
            compass=new Compass();
        }catch(IOException e){
            Log.e("Sensing","Failed to initialize Compass");
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                float head=compass.calculateHeadding();
                Log.i("Sensing",String.format("%7.3f",Compass.heading));
            } catch (IOException e) {
                Log.e("Sensing", "Error while reading compass heading");
            }

            try {
                this.sleep(100);
            } catch (InterruptedException e) {
             Log.e("Sensing", "Sleep Interrupted");
            }
        }
    }
}
