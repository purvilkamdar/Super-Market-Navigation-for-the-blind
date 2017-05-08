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

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText et;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Camera mCamera;
    private ImageView imgView;
    private static final String UART_DEVICE_NAME = "UART0";

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
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);
        TextView ip_address=(TextView)findViewById(R.id.textView2);
        ip_address.setText(getIPAddress(true));
        imgView=(ImageView)findViewById(R.id.imageView);
        et=(EditText)findViewById(R.id.editText2);
        startBackgroundThread();
        mCamera = Camera.getInstance();
        Log.i("pup","before initCam");
        mCamera.initCam(this, this.getApplicationContext(), mBackgroundHandler, mOnImageAvailableListener);
        final Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture();
            }
        });
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            uartDevice = manager.openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(uartDevice);
            setFlowControlEnabled(uartDevice,false);
        } catch (IOException e) {
            Log.w("pup", "Unable to access UART device", e);
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
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("InputThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
}
