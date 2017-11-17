package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import java.io.IOException;

import static java.lang.Math.sqrt;

/**
 * Created by parth.pachchigar on 10/27/17.
 */

public class Compass {
    private Magnetometer magnetometer;
    private Accelerometer accelerometer;
    public static double heading;
    public static int count=0;
    int[] magRaw=new int[3];
    int[] accRaw=new int[3];
    double accXnorm,accYnorm,pitch,roll,magXcomp,magYcomp,accZnorm;
    float[] scaledMag=new float[3];

// 838   848     0 -1280  -983 -32768
    private int magXmax=1210;
    private int magYmax=1245;
    private int magZmax=1178;
    private int magXmin=-1335;
    private int magYmin=-1142;
    private int magZmin=-1303;

    //private final double MAG_LPF_FACTOR = 0.4;
    //private final double ACC_LPF_FACTOR = 0.1;
    //int oldXMagRawValue = 0;
    //int oldYMagRawValue = 0;
    //int oldZMagRawValue = 0;
    //int oldXAccRawValue = 0;
    //int oldYAccRawValue = 0;
    //int oldZAccRawValue = 0;
    public Compass() throws IOException{
        try {
            magnetometer = new Magnetometer();
            magnetometer.init();
        }catch(IOException e){
            Log.e("Compass","Failed to initialize Magnetometer");
        }
        try{
            accelerometer=new Accelerometer();
            accelerometer.init();
        }catch(IOException e){
            Log.e("Compass","Failed to initialize Accelerometer");
        }
    }

    public int calculateHeadding() throws IOException {
        double head;
        magRaw=magnetometer.getRawValues();
        accRaw=accelerometer.getRawValues();

        magRaw[1]=-magRaw[1];
        accRaw[0] = -accRaw[0];
        accRaw[1] = -accRaw[1];

        //if (magRaw[0] > magXmax) magXmax = magRaw[0];
        //if (magRaw[1] > magYmax) magYmax = magRaw[1];
        //if (magRaw[2] > magZmax) magZmax = magRaw[2];

        //if (magRaw[0] < magXmin) magXmin = magRaw[0];
        //if (magRaw[1] < magYmin) magYmin = magRaw[1];
        //if (magRaw[2] < magZmin) magZmin = magRaw[2];


        //magRaw[0] =  (int) (magRaw[0]  * MAG_LPF_FACTOR + oldXMagRawValue*(1 - MAG_LPF_FACTOR));
        //magRaw[1] =  (int) (magRaw[1]  * MAG_LPF_FACTOR + oldYMagRawValue*(1 - MAG_LPF_FACTOR));
        //magRaw[2] =  (int) (magRaw[2]  * MAG_LPF_FACTOR + oldZMagRawValue*(1 - MAG_LPF_FACTOR));
        //accRaw[0] =  (int) (accRaw[0]  * ACC_LPF_FACTOR + oldXAccRawValue*(1 - ACC_LPF_FACTOR));
        //accRaw[1] =  (int) (accRaw[1]  * ACC_LPF_FACTOR + oldYAccRawValue*(1 - ACC_LPF_FACTOR));
        //accRaw[2] =  (int) (accRaw[2]  * ACC_LPF_FACTOR + oldZAccRawValue*(1 - ACC_LPF_FACTOR));


        //oldXMagRawValue = magRaw[0];
        //oldYMagRawValue = magRaw[1];
        //oldZMagRawValue = magRaw[2];
        //oldXAccRawValue = accRaw[0];
        //oldYAccRawValue = accRaw[1];
        //oldZAccRawValue = accRaw[2];



        //Apply hard iron calibration
        magRaw[0] -= (magXmin + magXmax) /2 ;
        magRaw[1] -= (magYmin + magYmax) /2 ;
        magRaw[2] -= (magZmin + magZmax) /2 ;

        //Apply soft iron calibration
        scaledMag[0]  = (float)(magRaw[0] - magXmin) / (magXmax - magXmin) * 2 - 1;
        scaledMag[1]  = (float)(magRaw[1] - magYmin) / (magYmax - magYmin) * 2 - 1;
        scaledMag[2]  = (float)(magRaw[2] - magZmin) / (magZmax - magZmin) * 2 - 1;



        head =  (180 * Math.atan2(magRaw[1],magRaw[0])/Math.PI);

        //Convert heading to 0 - 360
        if(head < 0)
            head += 360;


        //Normalize accelerometer raw values.
        accXnorm = accRaw[0]/sqrt(accRaw[0] * accRaw[0] + accRaw[1] * accRaw[1] + accRaw[2] * accRaw[2]);
        accYnorm = accRaw[1]/sqrt(accRaw[0] * accRaw[0] + accRaw[1] * accRaw[1] + accRaw[2] * accRaw[2]);
        accZnorm = accRaw[2]/sqrt(accRaw[0] * accRaw[0] + accRaw[1] * accRaw[1] + accRaw[2] * accRaw[2]);


        //Calculate pitch and roll (Original)
        pitch = Math.asin(accXnorm);
        roll = Math.asin(accYnorm/Math.cos(pitch));

        //pitch = Math.atan2(accXnorm,Math.sqrt((accYnorm*accYnorm) + (accZnorm*accZnorm)));
        //roll = Math.atan2(accYnorm,Math.sqrt((accXnorm*accXnorm) + (accZnorm*accZnorm)));

        //Calculate the new tilt compensated values
        magXcomp = magRaw[0]*Math.cos(pitch)+magRaw[02]*Math.sin(pitch);
        magYcomp = magRaw[0]*Math.sin(roll)*Math.sin(pitch)+magRaw[1]*Math.cos(roll)-magRaw[2]*Math.sin(roll)*Math.cos(pitch);


        //Calculate heading
        //head = 180*Math.atan2(magYcomp,magXcomp)/Math.PI;
        //double mag_norm,xmag,ymag,zmag;
        //mag_norm=Math.sqrt((magRaw[0]*magRaw[0])+(magRaw[1]*magRaw[1])+(magRaw[2]*magRaw[2]));
        //xmag=magRaw[0]/mag_norm;
        //ymag=magRaw[1]/mag_norm;
        //zmag=magRaw[2]/mag_norm;
        //head=Math.atan2(((-1)*ymag*Math.cos(roll) + zmag*Math.sin(roll) ) , (xmag*Math.cos(pitch) + ymag*Math.sin(pitch)*Math.sin(roll)+ zmag*Math.sin(pitch)*Math.cos(roll)));
        //Convert heading to 0 - 360
        head-=180;
        if(head < 0)
            head += 360;


        heading=head;
        //Log.i("Compass",String.format("Compensated  Heading %7.3f  \n", heading));
        return (int) (head);
    }
}
