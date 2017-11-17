package com.example.parthpachchigar.hardwaredevice;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String I2C_DEVICE_NAME = "I2C1";
    // I2C Slave Address
    private static final int I2C_ADDRESS = 0x1E;

    private static I2cDevice mDevice;
    int magXmax = 0;
    int magYmax = 0;
    int magZmax = 0;
    int magXmin = 0;
    int magYmin = 0;
    int magZmin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.CHANGE_WIFI_STATE)==PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_WIFI_STATE)==PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
        {

        }
        else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 1);
        }


        Wifi wifi=new Wifi(this, MainActivity.this);
        wifi.start();
        Sensing sense=new Sensing();
        sense.start();
        //while(HeadingBuffer.bufferEmpty()){
        //    continue;
        //}
        //Log.i("MainActivity","Got all 40 data");

        /*try {
            Magnetometer m=new Magnetometer();
            while(true){
                int[] magRaw=m.getRawValues();
                if (magRaw[0] > magXmax) magXmax = magRaw[0];
                if (magRaw[1] > magYmax) magYmax = magRaw[1];
                if (magRaw[2] > magZmax) magZmax = magRaw[2];

                if (magRaw[0] < magXmin) magXmin = magRaw[0];
                if (magRaw[1] < magYmin) magYmin = magRaw[1];
                if (magRaw[2] < magZmin) magZmin = magRaw[2];
                Log.i("Data",String.format("%5d %5d %5d %5d %5d %5d",magXmax,magYmax,magZmax,magXmin,magYmin,magZmin));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        TTS tts=new TTS();
        tts.init_TTS(this);
        tts.start();
        handler handle=new handler();
        handle.put("Application started");

        try {
            Pedometer pedometer=new Pedometer("BCM12",this);
            pedometer.setAsInput();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w("SensorDevice", "Unable to close I2C device2", e);
            }
        }
    }
    public byte[] readCalibration(I2cDevice device, int startAddress) throws IOException {
        // Read three consecutive register values
        byte[] data = new byte[6];
        device.readRegBuffer(startAddress, data, data.length);
        return data;
    }

}
