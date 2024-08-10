package com.example.qrcodereader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {

    private ZXingScannerView scannerView;

    private static final int PERMISSION_CODE = 1000;

    Button mCaptureBtn;
    Button mClockwiseBtn;
    Button mCounterClockwiseBtn;
    String resultCode = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCaptureBtn = findViewById(R.id.capture_image_btn);
        mClockwiseBtn = findViewById(R.id.clockwise_btn);
        mCounterClockwiseBtn = findViewById(R.id.counterclockwise_btn);

    }

    public void scanCode(View view) {

        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED) {
                        //request permission
                        String[] permission = {Manifest.permission.CAMERA};
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        //permission granted
                        setContentView(scannerView);
                        scannerView.startCamera();
                    }
                }
                else{
                    //Android build better than marshmallow level
                    setContentView(scannerView);
                    scannerView.startCamera();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @Override
        public void handleResult (Result result) {
            resultCode = result.getText();
            Toast.makeText(MainActivity.this, "You are now connected to:" + resultCode, Toast.LENGTH_SHORT).show();

            setContentView(R.layout.activity_main);
            scannerView.stopCamera();
            changeQRText();
        }
    }

    public void changeQRText() {
        final TextView QRText = findViewById(R.id.qr_text);
        QRText.setText(resultCode);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //permission from permission popup was denied
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}