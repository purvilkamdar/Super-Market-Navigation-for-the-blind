package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import java.io.IOException;

/**
 * Created by parth.pachchigar on 10/30/17.
 */

public class Compass_Purvil extends Thread {

    private Accelerometer acc;
    private Magnetometer mag;
    Compass_Purvil()
    {
        try {
            acc = new Accelerometer();
            mag = new Magnetometer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int[] FusionVectorCrossProduct(int[] acclero, int [] magneto)
    {
        int result[] = new int[3];
        result[0] = acclero[1]*magneto[2] - acclero[2]*magneto[1];
        result[1] = acclero[2]*magneto[0] - acclero[0]*magneto[2];
        result[2] = acclero[0]*magneto[1] - acclero[1]*magneto[0];
        return result;
    }
    public float FusionFastInverseSqrt(float x)
    {
        float halfx = 0.5f * x;
        float y = x;
        int i = (int) y;
        i = 0x5f3759df - (i >> 1);
        y = (float) i;
        y = y * (1.5f - (halfx * y * y));
        return y;
    }
    public int[] FusionVectorMultiplyScalar(int[] v,float scalar)
    {
        int result[] = new int[3];
        result[0] = (int)(v[0] * scalar);
        result[1] = (int)(v[1] * scalar);
        result[2] = (int)(v[2] * scalar);
        return result;
    }
    public int[] FusionVectorFastNormalise(int[] vector)
    {
        float normReciprocal = FusionFastInverseSqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return FusionVectorMultiplyScalar(vector, normReciprocal);
    }

    public int FUSION_RADIANS_TO_DEGREES(double radians)
    {
        int result = (int)((float)(radians) * (180.0f / Math.PI));
        return result;

    }

    @Override
    public void run()
    {
        while(true) {
            try {
                int current_Acc[] = acc.getRawValues();
                int current_Mag[] = mag.getRawValues();
                int cross_product[] = FusionVectorCrossProduct(current_Acc, current_Mag);
                int[] magneticWest = FusionVectorFastNormalise(cross_product);


                // Compute direction of magnetic north (Earth's x axis)
                int[] magneticNorth = FusionVectorFastNormalise(FusionVectorCrossProduct(magneticWest, current_Acc));

                double mw = (double) magneticWest[0];
                double mn = (double) magneticNorth[0];
                // Calculate angular heading relative to magnetic north
                int result = FUSION_RADIANS_TO_DEGREES(Math.atan2(mw, mn));
                Log.w("Heading:", String.valueOf(result));
                Thread.sleep(100);
            } catch (Exception e) {
                Log.w("Compass_Purvil", e.toString());
            }
        }
        // Compute direction of 'magnetic west' (Earth's y axis)

    }
}