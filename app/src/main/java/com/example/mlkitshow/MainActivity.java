package com.example.mlkitshow;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.common.MLApplication;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String APIKEY = "CgB6e3x9E6dIQmt4QyjGtsM4KDQU7ILPGxn1VFI/bGzeRo4FgMCyX1GnjodF+vZgcjutRHNQHppafS8AdzS1QgUe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.scantext_imgview).setOnClickListener(this);
        findViewById(R.id.scanqr_imgview).setOnClickListener(this);
        findViewById(R.id.landmark_imgview).setOnClickListener(this);
        findViewById(R.id.face_imgview).setOnClickListener(this);
        MLApplication.getInstance().setApiKey(APIKEY);
        requestPermission();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scantext_imgview) {
            Intent i = new Intent(MainActivity.this, ScanTextActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.scanqr_imgview) {
            Intent i = new Intent(MainActivity.this, ScanQRActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.landmark_imgview) {
            Intent i = new Intent(MainActivity.this,LandmarkActivity.class);
            startActivity(i);
        }
        if (v.getId() == R.id.face_imgview) {
            Intent i = new Intent(MainActivity.this,ScanFaceActivity.class);
            startActivity(i);
        }
    }

    private void requestPermission() {
        final String[] permissions = new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 1001);
            return;
        }

    }
}