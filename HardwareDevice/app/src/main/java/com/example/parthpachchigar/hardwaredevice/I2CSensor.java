package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;

/**
 * Created by parth.pachchigar on 10/26/17.
 */

public class I2CSensor {
    private static final String I2C_DEVICE_NAME = "I2C1";
    // I2C Slave Address
    private static final int I2C_ADDRESS = 0x1E;

    private static I2cDevice mDevice;

    public I2CSensor() throws IOException{

        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getI2cBusList();
        if (deviceList.isEmpty()) {
            Log.i("SensorDevice", "No I2C bus available on this device.");
        } else {
            Log.i("SensorDevice", "List of available devices: " + deviceList);
        }
        try {
            if(mDevice==null) {
                mDevice = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS);
            }
        }catch(IOException e){
            Log.e("I2CSensor","Failed to open I2C Device");
        }finally{
        }
    }

    public void writeI2C(int register,int data) throws IOException{
        mDevice.writeRegByte(register,(byte)data);
    }

    public byte[] readI2C(int startAddress, int numberOfRegisters) throws IOException {
        // Read three consecutive register values
        byte[] data = new byte[numberOfRegisters];
        mDevice.readRegBuffer(startAddress, data, data.length);
        return data;

    }
}
