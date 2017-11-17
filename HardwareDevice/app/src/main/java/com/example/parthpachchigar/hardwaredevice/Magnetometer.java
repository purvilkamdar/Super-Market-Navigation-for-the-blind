package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

/**
 * Created by parth.pachchigar on 10/26/17.
 */

public class Magnetometer extends I2CSensor {


    public Magnetometer() throws IOException {
    }

    public void init() throws IOException{
        try {
            writeI2C(0x24, 0xf0);
            writeI2C(0x25, 0x60);
            writeI2C(0x26, 0x00);
        }catch(IOException e){
            Log.e("Magnetometer","Failed to complete init method");
        }
    }

    public int[] getRawValues() throws IOException{
        int[] mag=new int[3];
        byte[] a = readI2C(0x80 | 0x08, 6);
        mag[0] = (int) ((a[0] | a[1] << 8));
        mag[1] = (int) ((a[2] | a[3] << 8));
        mag[2] = (int) ((a[4] | a[5] << 8));
        return mag;
    }



}
