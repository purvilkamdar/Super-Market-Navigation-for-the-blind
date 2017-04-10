package com.example.admin.supermarketnav;

import android.Manifest;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture();
            }
        });

    }

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
        String msg="";
        if (imageBytes != null) {
            for(int i=0;i<imageBytes.length;i++){
                msg+=imageBytes[i]+" ";
            }
        }
        et.setText(msg);
    }

    protected void onDestroy() {
        super.onDestroy();

        mBackgroundThread.quitSafely();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("InputThread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
}
