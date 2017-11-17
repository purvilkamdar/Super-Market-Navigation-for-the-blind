package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by parth.pachchigar on 10/30/17.
 */

public class sample extends Thread {
    private Accelerometer acc;
    private Magnetometer mag;
    ArrayList<Double> compassBearingFilter;

    sample() {
        try {
            acc = new Accelerometer();
            mag = new Magnetometer();
            compassBearingFilter = new ArrayList<Double>(0);
        } catch (Exception e) {
            Log.w("Error in compass:", e.toString());
        }
    }

    @Override
    public void run() {

            double[] lastAngles = {0, 0, 0};
            while(true){
                int temp_acc[] = new int[3];
                int temp_mag[] = new int[3];
                try {
                    temp_acc = acc.getRawValues();
                    temp_mag = mag.getRawValues();
                } catch (Exception e) {
                    Log.w("Error in compi", e.toString());
                }
                int mx = temp_mag[0];
                int my = temp_mag[1];
                int mz = temp_mag[2];
                int ax = temp_acc[0];
                int ay = temp_acc[1];
                int az = temp_acc[2];


                //Roll Angle - about axis 0
                //  tan(roll angle) = gy/gz
                //  Use Atan2 so we have an output os (-180 - 180) degrees
                double rollAngle = Math.atan2(ay, az);

                //Pitch Angle - about axis 1
                //  tan(pitch angle) = -gx / (gy * sin(roll angle) * gz * cos(roll angle))
                //  Pitch angle range is (-90 - 90) degrees
                double pitchAngle = Math.atan(-ax / (ay * Math.sin(rollAngle) + az * Math.cos(rollAngle)));

                //Yaw Angle - about axis 2
                //  tan(yaw angle) = (mz * sin(roll) – my * cos(roll)) /
                //                   (mx * cos(pitch) + my * sin(pitch) * sin(roll) + mz * sin(pitch) * cos(roll))
                //  Use Atan2 to get our range in (-180 - 180)
                //
                //  Yaw angle == 0 degrees when axis 0 is pointing at magnetic north
                double yawAngle = Math.atan2(mz * Math.sin(rollAngle) - my * Math.cos(rollAngle),
                        mx * Math.cos(pitchAngle) + my * Math.sin(pitchAngle) * Math.sin(rollAngle) + mz * Math.sin(pitchAngle) * Math.cos(rollAngle));

                double[] angles = {rollAngle, pitchAngle, yawAngle};

                //we low-pass filter the angle data so that it looks nicer on-screen
                try {
                    //make sure the filter buffer doesn't have values passing the -180<->180 mark
                    //Only for Roll and Yaw - Pitch will never have a sudden switch like that
                    for (int i = 0; i < 3; i += 2) {
                        if (Math.abs(angles[i] - lastAngles[i]) > 3)
                            for (int j = 0; j < compassBearingFilter.size(); j++) {
                                double temp_value;
                                temp_value = compassBearingFilter.get(j);
                                if (angles[i] > lastAngles[i]) {

                                    temp_value += 360 * Math.PI / 180.0;
                                    compassBearingFilter.set(j, temp_value);

                                } else {
                                    temp_value -= 360 * Math.PI / 180.0;
                                    compassBearingFilter.set(j, temp_value);
                                }
                            }
                    }

                    lastAngles = (double[]) angles.clone();

                    for (int i = 0; i < 3; i++) {
                        compassBearingFilter.add(angles[i]);
                    }
                    if (compassBearingFilter.size() > 10)
                        compassBearingFilter.remove(0);

                    yawAngle = pitchAngle = rollAngle = 0;

                    rollAngle += compassBearingFilter.get(0);
                    pitchAngle += compassBearingFilter.get(1);
                    yawAngle += compassBearingFilter.get(2);

                    yawAngle /= compassBearingFilter.size();
                    pitchAngle /= compassBearingFilter.size();
                    rollAngle /= compassBearingFilter.size();

                    //Convert radians to degrees for display
                    double compassBearing = yawAngle * (180.0 / Math.PI);

                    Log.w("Angle", String.valueOf(compassBearing));
                    //pitchAngleTxt.Text = (pitchAngle * (180.0 / Math.PI)).ToString("F1") + "°";
                    //rollAngleTxt.Text = (rollAngle * (180.0 / Math.PI)).ToString("F1") + "°";
                } catch (Exception e) {

                }

        }
    }
}