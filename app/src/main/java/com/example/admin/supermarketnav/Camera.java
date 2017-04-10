package com.example.admin.supermarketnav;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;

import java.util.Collections;

import static android.content.Context.CAMERA_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Admin on 02-04-2017.
 */

public class Camera extends Activity{

    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int MAX_IMAGES = 1;
    private static final String TAG = "Camera.java";
    private static Camera cam=null;

    private ImageReader imageReader;
    private CameraDevice cameraDevice;
    private CameraCaptureSession camCaptureSession;


    public void initCam(Activity act, Context context,
                        Handler backgroundHandler,
                        ImageReader.OnImageAvailableListener imageListener) {
        if(ContextCompat.checkSelfPermission(context,"Manifest.permission.Camera")==PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(act, new String[] {Manifest.permission.CAMERA}, 1);
        }
        CameraManager manager = (CameraManager) context.getSystemService(CAMERA_SERVICE);

        String[] camIds = {};
        try {
            camIds = manager.getCameraIdList();

        } catch (CameraAccessException e) {
            Log.d(TAG, "Cam access exception getting IDs", e);
        }
        if (camIds.length < 1) {
            Log.d(TAG, "No cameras found");
            return;
        }
        String id = camIds[0];
        CameraCharacteristics caracteristicas=null;
        try {
            caracteristicas = manager.getCameraCharacteristics(id);
        } catch (CameraAccessException e) {
            Log.d(TAG, "Cam access exception getting IDs", e);
        }
        StreamConfigurationMap map = caracteristicas.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] mPreviewSize = map.getOutputSizes(ImageFormat.JPEG);
        IMAGE_WIDTH=mPreviewSize[0].getWidth();
        IMAGE_HEIGHT=mPreviewSize[0].getHeight();
        imageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT, ImageFormat.JPEG, MAX_IMAGES);
        imageReader.setOnImageAvailableListener(imageListener, backgroundHandler);

        try {
            manager.openCamera(id, mStateCallback, backgroundHandler);
        } catch (CameraAccessException cae) {
            Log.d(TAG, "Camera access exception", cae);
        }

    }

    private final CameraDevice.StateCallback mStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    cameraDevice.close();
                }

                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.i(TAG, "Cam initialized");
                    cameraDevice=camera;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    cameraDevice.close();
                }
            };
    public void shutDown() {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
    }
    private Camera(){

    }
    public static Camera getInstance(){
        if(cam==null){
            cam=new Camera();
        }
        return cam;
    }
    public void takePicture() {
        if (cameraDevice == null) {
            Log.w(TAG, "Cannot capture image. Camera not initialized.");
            return;
        }

        // Here, we create a CameraCaptureSession for capturing still images.
        try {
            cameraDevice.createCaptureSession(
                    Collections.singletonList(imageReader.getSurface()),
                    mSessionCallback,
                    null);
        } catch (CameraAccessException cae) {
            Log.d(TAG, "access exception while preparing pic", cae);
        }
    }

    private final CameraCaptureSession.StateCallback mSessionCallback =
            new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    // When the session is ready, we start capture.
                    camCaptureSession = cameraCaptureSession;
                    triggerImageCapture();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.w(TAG, "Failed to configure camera");
                }
            };

    private void triggerImageCapture() {
        try {
            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);

            camCaptureSession.capture(captureBuilder.build(), mCaptureCallback, null);
        } catch (CameraAccessException cae) {
            Log.d(TAG, "camera capture exception");
        }
    }

    private final CameraCaptureSession.CaptureCallback mCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {


                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    if (session != null) {
                        session.close();
                        camCaptureSession = null;
                        Log.d(TAG, "CaptureSession closed");
                    }
                }
            };

}