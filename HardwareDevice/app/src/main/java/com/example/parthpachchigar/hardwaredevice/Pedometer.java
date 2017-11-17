package com.example.parthpachchigar.hardwaredevice;

import android.content.Context;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.Random;

/**
 * Created by parth.pachchigar on 10/28/17.
 */

public class Pedometer {
    private String GPIOName;
    private Gpio mGpio;
    Context mContext;

    public Pedometer(String name, Context context) throws IOException{
        PeripheralManagerService manager = new PeripheralManagerService();
        GPIOName=name;
        mGpio = manager.openGpio(GPIOName);
        mContext=context;
    }
    public void setAsInput() throws IOException {
        // Initialize the pin as an input
        mGpio.setDirection(Gpio.DIRECTION_IN);
        // Low voltage is considered active
        mGpio.setActiveType(Gpio.ACTIVE_LOW);

        // Register for all state changes
        mGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
        mGpio.registerGpioCallback(mGpioCallback);
    }
    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active low pin state
            try {
                if (gpio.getValue()) {
                    // Pin is LOW
                    Localization localization=new Localization(mContext);
                    Random r = new Random();
                    int Low = 178;
                    int High = 181;
                    int Result = r.nextInt(High-Low) + Low;
                    localization.calculateCord(Result,0.4375);

                } else {
                    // Pin is HIGH
                }
            } catch (IOException e) {
                Log.e("Pedometer", "Error reading Pedometer");
            }
            // Continue listening for more interrupts
            return true;
        }
    };
}
