package com.example.parthpachchigar.hardwaredevice;

import java.io.IOException;

/**
 * Created by parth.pachchigar on 10/26/17.
 */

public class Accelerometer extends I2CSensor {
    public Accelerometer() throws IOException{

    }
    public void init() throws IOException{
        writeI2C(0x20, 0x67);
        writeI2C(0x21, 0x20);
    }

    public int[] getRawValues() throws IOException{
        int[] acc=new int[3];
        byte[] a = readI2C(0x80 | 0x28, 6);
        acc[0] = (int) ((a[0] | a[1] << 8));
        acc[1] = (int) ((a[2] | a[3] << 8));
        acc[2] = (int) ((a[4] | a[5] << 8));
        return acc;
    }
}
