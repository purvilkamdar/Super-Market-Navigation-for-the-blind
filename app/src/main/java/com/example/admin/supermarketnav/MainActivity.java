package com.example.admin.supermarketnav;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.*;
import android.util.*;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import cz.msebera.android.httpclient.Header;

import com.example.admin.supermarketnav.Magnetometer;
import com.example.admin.supermarketnav.UploadDatatoGraph;


public class MainActivity extends AppCompatActivity {






            static int MPU9250_ADDRESS = 0x68;
            static int MAG_ADDRESS = 0x0C;

    static int    GYRO_FULL_SCALE_250_DPS =   0x00;
    static int    GYRO_FULL_SCALE_500_DPS  =  0x08;
    static int    GYRO_FULL_SCALE_1000_DPS = 0x10;
    static int    GYRO_FULL_SCALE_2000_DPS  = 0x18;

    static int    ACC_FULL_SCALE_2_G    =    0x00;
    static int    ACC_FULL_SCALE_4_G     =   0x08;
    static int    ACC_FULL_SCALE_8_G      =  0x10;
    static int    ACC_FULL_SCALE_16_G      = 0x18;

    int accelcounter;
    int gyrocounter;
    int motioncounter;
    int ultrasoniccounter;
    String clear_call[] = new String[1];
    String accelero_Call[] = new String[5];
    String gyro_Call[] = new String[5];
    String motion_Call[] = new String[3];
    String ultra_Call[] = new String[3];



    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private UploadDatatoGraph clear;
    private UploadDatatoGraph acc;
    private UploadDatatoGraph gyr;

    private EditText et;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Camera mCamera;
    //private UploadFileAsync uploadFileAsync;
    private ImageView imgView;
    private static final String UART_DEVICE_NAME = "UART0";
    private static final String I2C_DEVICE_NAME = "I2C1";
    private I2cDevice sl;
    private static int slave_address = 0x68;
    private static int magnetometer_address = 0x0C;



    //Ankit's code
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String GPIO_NAME = "BCM6";
    private static final String GPIO_TRIG = "BCM21";
    private static final String GPIO_ECHO = "BCM20";
    private Gpio mGpio;
    private Gpio mGpioTrig;
    private Gpio mGpioEcho;
    long time1, time2;
    private Handler mGpioCallbackUltra;

    int keepBusy;
    int number=0;                    //Interrupt times


    PeripheralManagerService manager;

    private UartDevice uartDevice;

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }


    // This function read Nbytes bytes from I2C device at address Address.
// Put read bytes starting at register Register in the Data array.
    public byte[] I2Cread(int Address, int Register, int Nbytes) {
        byte data[] = new byte[Nbytes];
        // Set register address
        try {
            sl = manager.openI2cDevice(I2C_DEVICE_NAME, Address);
        } catch (Exception e) {
            Log.w("Purvil", "unable to open from read", e);
        }

        try {
            sl.readRegBuffer(Register, data, Nbytes);
        }
        catch (Exception e)
        {
            Log.w("Purvil","Unable to read data");
        }


        if(sl!=null)
        {
            try {
                sl.close();
            }
            catch (Exception e)
            {
                Log.w("Purvil","Unable to close from read",e);
            }

            sl=null;
        }
        return data;
    }


    // Write a byte (Data) in device (Address) at register (Register)
    void I2CwriteByte(int Address, int Register, byte Data)
    {
        // Set register address
        try {
            sl=manager.openI2cDevice(I2C_DEVICE_NAME,Address);
        }
        catch (Exception e)
        {
            Log.w("Purvil","Unable to open from write",e);
        }
        try {
            sl.writeRegByte(Register,Data);
        }
        catch (Exception e)
        {
            Log.w("Purvil","Unable to write from I2cwrite",e);
        }
        if(sl!=null)
        {
            try {
                sl.close();
            }
            catch (Exception e)
            {
                Log.w("Purvil","Unable to close I2c",e);
            }
            sl=null;
        }
    }


    // Initializations
    public void setup()
    {
        // Arduino initializations


        // Configure gyroscope range
        I2CwriteByte(MPU9250_ADDRESS,27,(byte) GYRO_FULL_SCALE_2000_DPS);
        // Configure accelerometers range
        I2CwriteByte(MPU9250_ADDRESS,28,(byte) ACC_FULL_SCALE_16_G);
        // Set by pass mode for the magnetometers
        I2CwriteByte(MPU9250_ADDRESS,0x37,(byte) 0x02);

        // Request first magnetometer single measurement
        I2CwriteByte(MAG_ADDRESS,0x0A,(byte) 0x01);


    }


    long cpt=0;
    // Main loop, read and display data








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clear = new UploadDatatoGraph();
        clear_call[0]="clear";
        new UploadDatatoGraph().execute(clear_call);
        acc = new UploadDatatoGraph();
        gyr= new UploadDatatoGraph();
        setContentView(R.layout.activity_main);
        TextView ip_address = (TextView) findViewById(R.id.textView2);
        ip_address.setText(getIPAddress(true));
        imgView = (ImageView) findViewById(R.id.imageView);
        et = (EditText) findViewById(R.id.editText2);


        manager = new PeripheralManagerService();
        try {
            uartDevice = manager.openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(uartDevice);
            setFlowControlEnabled(uartDevice, false);
        } catch (IOException e) {
            Log.w("pup", "Unable to access UART device", e);
        }

        //Ankit's code

        try {
            mGpioEcho = manager.openGpio(GPIO_ECHO);    // Ultrasonic ECHO Pin
            configureGpioUltrasonicEcho(mGpioEcho);
            mGpioTrig = manager.openGpio(GPIO_TRIG);    // Ultrasonic TRIG Pin
            configureGpioUltrasonicTrigger(mGpioTrig);
        }
        catch (Exception e)
        {
            Log.w("Ankit:","Exception:",e);
        }
/*
            // Prepare handler for GPIO callback
            HandlerThread handlerThread = new HandlerThread("callbackHandlerThread");
            handlerThread.start();
            mGpioCallbackUltra = new Handler(handlerThread.getLooper());

            // Prepare handler to send triggers
            HandlerThread triggerHandlerThread = new HandlerThread("triggerHandlerThread");
            triggerHandlerThread.start();
            ultrasonicTriggerHandler = new Handler(triggerHandlerThread.getLooper());
*/







    //ultrasonicTriggerHandler.post(triggerRunnable);
        new Thread(){
        @Override
        public void run() {
            try {
                while (true) {
                    readDistanceSync();
                    Thread.sleep(300);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }.start();


        //Purvil's code
        setup();
        while (true)
        {
            // _______________
            // ::: Counter :::

            // Display data counter



            // ____________________________________
            // :::  accelerometer and gyroscope :::

            // Read accelerometer and gyroscope
            byte Buf[] = new byte[14];
            Buf=I2Cread(MPU9250_ADDRESS,0x3B,14);


            // Create 16 bits values from 8 bits data

            // Accelerometer
            int ax=-(Buf[0]<<8 | Buf[1]);
            int ay=-(Buf[2]<<8 | Buf[3]);
            int az=Buf[4]<<8 | Buf[5];

            // Gyroscope
            int gx=-(Buf[8]<<8 | Buf[9]);
            int gy=-(Buf[10]<<8 | Buf[11]);
            int gz=Buf[12]<<8 | Buf[13];

            // Display values

            // Accelerometer
            Log.w ("Purvil","ax="+ax);
            Log.w ("Purvil","ay="+ay);
            Log.w ("Purvil","az="+az);;

            // Gyroscope
            Log.w ("Purvil","gx="+gx);
            Log.w ("Purvil","gy="+gy);
            Log.w ("Purvil","gz="+gz);;


            // _____________________
            // :::  Magnetometer :::


            // Read register Status 1 and wait for the DRDY: Data Ready

            byte ST1[]= new byte[1];
            /*do
            {

                try {
                    ST1 = I2Cread(MAG_ADDRESS, 0x02, 1);
                    Log.w("Purvil:","Trying to read magnetometer value");
                }
                catch (Exception e)
                {
                    Log.w("Purvil","Unable to read mag adress from while",e);
                }
            }
            while (!(((int)ST1[0]&0x01)==0x01));
*/
            // Read magnetometer data
            byte Mag[] = new byte[7];
            Mag=I2Cread(MAG_ADDRESS,0x03,7);


            // Create 16 bits values from 8 bits data

            // Magnetometer
            int mx=-(Mag[3]<<8 | Mag[2]);
            int my=-(Mag[1]<<8 | Mag[0]);
            int mz=-(Mag[5]<<8 | Mag[4]);


            // Magnetometer
            Log.w ("Purvil","mx="+(mx+200));
            Log.w ("Purvil","my="+(my-70));
            Log.w ("Purvil","mz="+(mz-700));


            // End of line

            try {
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                Log.w("Purvil","Exception in Thread sleep",e);
            }

            accelero_Call[0]="a";
            accelero_Call[1]=Integer.toString(accelcounter);
            accelero_Call[2]=Integer.toString(ax);
            accelero_Call[3]=Integer.toString(ay);
            accelero_Call[4]=Integer.toString(az);

            gyro_Call[0]="g";
            gyro_Call[1]=Integer.toString(gyrocounter);
            gyro_Call[2]=Integer.toString(gx);
            gyro_Call[3]=Integer.toString(gy);
            gyro_Call[4]=Integer.toString(gz);

            new UploadDatatoGraph().execute(accelero_Call);
            new UploadDatatoGraph().execute(gyro_Call);
            accelcounter++;
            gyrocounter++;

//  delay(100);
        }

}










    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(38400);
        uart.setDataSize(8);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }


    public void setFlowControlEnabled(UartDevice uart, boolean enable) throws IOException {
        if (enable) {
            // Enable hardware flow control
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_AUTO_RTSCTS);
        } else {
            // Disable flow control
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);
        }
    }

    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = 1;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.d("pup", "Read " + buffer[0] + " bytes from peripheral");
        }
    }

    private UartDeviceCallback mUartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            try {
                readUartBuffer(uart);
            } catch (IOException e) {
                Log.w("pup", "Unable to access UART device", e);
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w("pup", uart + ": Error event " + error);
        }
    };



    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // Get the raw image bytes
                    Image image = reader.acquireLatestImage();
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();

                    onPictureTaken(imageBytes);
                }
            };






    private void onPictureTaken(byte[] imageBytes) {
        OutputStream output = null;
        File file=new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
        if (imageBytes != null) {

            try {
                output = new FileOutputStream(file);
                output.write(imageBytes);
                if (null != output) {
                    Log.i("pup","File opened");
                    output.close();
                }
            } catch (Exception e) {
                Log.w("pup", "File Exception:", e);
            }
        }
        final Bitmap bm=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic.jpg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgView.setImageBitmap(bm);
            }
        });
        new UploadFileAsync().execute("");  //For uploading the image to webserver
        /*
        if(imageBytes!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;

            final Bitmap bm= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length,options);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgView.setImageBitmap(bm);
                }
            });


        }*/

    }




    //Ankit's code
    private GpioCallback mCallback  = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {

                if (gpio.getValue() == false){
                    // The end of the pulse on the ECHO pin

                    time2 = System.nanoTime();

                    // Measure how long the echo pin was held high (pulse width)
                    long pluseWidth = time2 - time1;
                    //Log.d(TAG, "pluseWidth: " + pluseWidth);

                    // Calculate the distance (cm) with the speed of the sound
                    double distance = (pluseWidth / 1000000000.0) * 340.0 / 2.0 * 100.0;

                    // Or use the constant from the datasheet
                    //double distance = (pluseWidth / 1000.0 ) / 58.23 ; //cm
                    Log.i(TAG, "distance: " + distance + " cm");

                    //Log.i(TAG, "Echo ENDED!");

                } else {
                    // The pulse arrived on ECHO pin
                    time1 = System.nanoTime();

                    //Log.i(TAG, "Echo ARRIVED!");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 6. Return true to keep callback active.
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w(TAG, gpio + ": Error event " + error);
        }
    };
    /*
    protected void readDistanceAsnyc() throws IOException, InterruptedException {
        // Just to be sure, set the trigger first to false
        mGpioTrig.setValue(false);
        Thread.sleep(0,2000);

        // Hold the trigger pin high for at least 10 us
        mGpioTrig.setValue(true);
        Thread.sleep(0,10000); //10 microsec

        // Reset the trigger pin
        mGpioTrig.setValue(false);
    }*/

    protected void readDistanceSync() throws IOException, InterruptedException {

        // Just to be sure, set the trigger first to false
        mGpioTrig.setValue(false);
        Thread.sleep(0,2000);

        // Hold the trigger pin HIGH for at least 10 us
        mGpioTrig.setValue(true);
        Thread.sleep(0,10000); //10 microsec

        // Reset the trigger pin
        mGpioTrig.setValue(false);

        // Wait for pulse on ECHO pin
        while (mGpioEcho.getValue() == false) {
            //long t1 = System.nanoTime();
            //Log.d(TAG, "Echo has not arrived...");

            // keep the while loop busy
            keepBusy = 0;

            //long t2 = System.nanoTime();
            //Log.d(TAG, "diff 1: " + (t2-t1));
        }
        time1 = System.nanoTime();
        Log.i(TAG, "Echo ARRIVED!");

        // Wait for the end of the pulse on the ECHO pin
        while (mGpioEcho.getValue() == true) {
            //long t1 = System.nanoTime();
            //Log.d(TAG, "Echo is still coming...");

            // keep the while loop busy
            keepBusy = 1;

            //long t2 = System.nanoTime();
            //Log.d(TAG, "diff 2: " + (t2-t1));
        }
        time2 = System.nanoTime();
        Log.i(TAG, "Echo ENDED!");

        // Measure how long the echo pin was held high (pulse width)
        long pulseWidth = time2 - time1;

        // Calculate distance in centimeters. The constants
        // are coming from the datasheet, and calculated from the assumed speed
        // of sound in air at sea level (~340 m/s).
        double distance = (pulseWidth / 1000.0 ) / 58.23 ; //cm

        // or we could calculate it withe the speed of the sound:
        //double distance = (pulseWidth / 1000000000.0) * 340.0 / 2.0 * 100.0;

        Log.i(TAG, "distance: " + distance + " cm");
        ultrasoniccounter++;
        ultra_Call[0]="u";
        ultra_Call[1]=Integer.toString(ultrasoniccounter);
        if(distance>380)
            distance=360;
        ultra_Call[2]=Double.toString(distance);

        new UploadDatatoGraph().execute(ultra_Call);


    }
    /*
    public void configureGpioMicrowave(Gpio gpio) throws IOException {
        // Initialize the pin as an input
        gpio.setDirection(Gpio.DIRECTION_IN);

        // Low voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_LOW);

        // Register for all falling edge
        gpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
        gpio.registerGpioCallback(mGpioCallback);
    }*/

    public void configureGpioUltrasonicTrigger(Gpio gpio) throws IOException {
        // Initialize the pin as an input

        gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

    }

    public void configureGpioUltrasonicEcho(Gpio gpio) throws IOException {
        // Initialize the pin as an input


        gpio.setDirection(Gpio.DIRECTION_IN);
        // High voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_HIGH);
        // Register for all falling edge
        gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        gpio.registerGpioCallback(mCallback,mGpioCallbackUltra);
    }









    @Override
    protected void onStart() {
        super.onStart();
        try {
            uartDevice.registerUartDeviceCallback(mUartCallback);
            Log.i("pup","Uart registration done.");
        }catch(Exception e){
            Log.w("pup", "UART not registered", e);
        }
    }
    protected void onStop() {
        super.onStop();
        // Interrupt events no longer necessary
        uartDevice.unregisterUartDeviceCallback(mUartCallback);
    }

    protected void onDestroy() {
        super.onDestroy();

        mBackgroundThread.quitSafely();
        if (uartDevice != null) {
            try {
                uartDevice.close();
                uartDevice = null;
            } catch (IOException e) {
                Log.w("pup", "Unable to close UART device", e);
            }
        }
        if (mGpio != null) {
            try {
                mGpio.close();
                mGpio = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPIO", e);
            }

        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("InputThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
}
