package com.lagarto.barcodedetect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{



    //TODO (3) Constante para verificar permisos
    static final int CAMERA_PERMISSION_REQUEST_CODE = 24;

    // TODO (1) En graddle poner la dependencia de Google Play services
    // TODO (5) referencia la detector de códigos
    private BarcodeDetector mBarcodeDetector;

    // TODO (7) refeencias de los views
    private SurfaceView mCameraView;
    private TextView mBarcodeInfo;

    // TODO (10) referencia al camera Source
    private CameraSource mCameraSource;

    private boolean previewIsRunning = false;

    private String codeString = "nada leido";

    private Vibrator mVibrator;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO (4) Verificar permisos
        // Check if the necessary permissions are granted.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "Se requieren permisos de cámara para usar esta aplicación", Toast.LENGTH_LONG).show();
            this.finish();
        }

        mVibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);



        // TODO (8) Obtener referencias de los views
        mCameraView = (SurfaceView)findViewById(R.id.camera_view);
        mBarcodeInfo = (TextView)findViewById(R.id.code_info);

        // TODO (9) Construir el detector
        mBarcodeDetector = new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        /*//TODO (11) Contruir el camera source
        mCameraSource = new CameraSource
                .Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();*/

        // TODO () inicar captura de la cámara.
        startCamera();


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        myStartPreview();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        myStopPreview();
    }

    private void buildCameraSource()
    {
        //TODO (11) Contruir el camera source
        mCameraSource = new CameraSource
                .Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
    }

    // safe call to start the preview
    // if this is called in onResume, the surface might not have been created yet
    // so check that the camera has been set up too.
    public void myStartPreview()
    {
        if (!previewIsRunning && (mCameraSource != null))
        {
            try
            {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)
                 == PackageManager.PERMISSION_GRANTED)
                    mCameraSource.start(mCameraView.getHolder());
                 else
                 {
                     Toast.makeText(MainActivity.this, "Se requieren permisos de cámara para usar esta aplicación", Toast.LENGTH_LONG).show();
                     MainActivity.this.finish();
                 }

            }
            catch (IOException ie)
            {
                Log.e("CAMERA SOURCE", ie.getMessage());
            }

            mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>()
            {
                @Override
                public void release()
                {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections)
                {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                    if (barcodes.size() != 0)
                    {

                        codeString = barcodes.valueAt(0).displayValue;
                        mVibrator.vibrate(500);

                        mBarcodeInfo.post(new Runnable()
                        {
                            // Use the post method of the TextView
                            public void run()
                            {
                                mBarcodeInfo.setText(barcodes.valueAt(0).displayValue);
                            }
                        });

                        goToNextActivity();

                    }
                }
            });

            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    public void myStopPreview()
    {
        if (previewIsRunning && (mCameraSource != null))
        {
            mCameraSource.stop();
            previewIsRunning = false;
        }
    }

    private void goToNextActivity()
    {
        Intent intent = new Intent(this, Nextctivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, codeString);
        startActivity(intent);
        finish();


    }

    private void startCamera()
    {
        //TODO (13) agregar el callback para el view de la cámara
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                buildCameraSource();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
                myStartPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                myStopPreview();
                mCameraSource.release();
                mCameraSource = null;

                Log.d("Main Activity", "Camera source stopped. *****");
            }
        });

    }



}
