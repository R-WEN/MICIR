package com.example.micir;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static android.graphics.ImageFormat.*;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyCarema extends Activity {


    private static final SparseIntArray ORIENTATIONS=new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private SurfaceView cameraView;
    private ImageButton picbtn;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private Handler childHanler,mainHandler;
    private String CameraID;//0為後,1為前
    private ImageReader imageReader;
    private CameraCaptureSession cameraCaptureSession;
    private CameraDevice cameraDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_carema);
        cameraView=(SurfaceView)findViewById(R.id.CameraSurfaceView);
        picbtn=(ImageButton)findViewById(R.id.pic_button);
        iniView();

        picbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void iniView(){
        surfaceHolder=cameraView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                iniCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraDevice!=null){
                    cameraDevice.close();
                    MyCarema.this.cameraDevice=null;
                }
            }
        });
    }

    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            takePreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if (cameraDevice!=null){
                cameraDevice.close();
                MyCarema.this.cameraDevice=null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(MyCarema.this,"相機開啟失敗",Toast.LENGTH_SHORT).show();
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void iniCamera2(){
        HandlerThread handlerThread=new HandlerThread("Camera2");
        handlerThread.start();
        childHanler=new Handler(handlerThread.getLooper());
        mainHandler=new Handler(getMainLooper());
        CameraID=""+ CameraCharacteristics.LENS_FACING_FRONT;
        imageReader=ImageReader.newInstance(1080,1920, JPEG,1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {


            @Override
            public void onImageAvailable(ImageReader reader) {
                cameraDevice.close();
                cameraView.setVisibility(View.GONE);
                //取得照片
                Image img=reader.acquireNextImage();
                ByteBuffer buffer=img.getPlanes()[0].getBuffer();
                byte[] bytes=new byte[buffer.remaining()];
                buffer.get(bytes);
                final Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Intent intent=new Intent();
                intent.putExtra("CameraPic",bitmap);
                setResult(1,intent);
                finish();



            }
        },mainHandler);

        cameraManager=(CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                return;
            }
            cameraManager.openCamera(CameraID,stateCallback,mainHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void takePreview(){
        try {
            final CaptureRequest.Builder previewRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    if (cameraDevice ==null){
                        return;
                    }
                    cameraCaptureSession=session;
                    try{
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(previewRequest,null,childHanler);
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(MyCarema.this,"相機配置失敗",Toast.LENGTH_SHORT).show();
                }
            },childHanler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }



    private void takePicture(){
        if (cameraDevice == null) return;
        final CaptureRequest.Builder captureRequestBuilder;
        try{
            captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            CaptureRequest captureRequest=captureRequestBuilder.build();
            cameraCaptureSession.capture(captureRequest,null,childHanler);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
