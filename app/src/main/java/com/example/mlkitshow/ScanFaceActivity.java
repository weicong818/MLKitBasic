package com.example.mlkitshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mlkitshow.camera.LensEnginePreview;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceEmotion;

import java.io.IOException;

//1.define a reset button and lensEnginePreview
//2.create face Analyzer,LensEngine and startLensEngine
//3.Handle and display the return result
//4.close the lensengine
public class ScanFaceActivity extends AppCompatActivity implements View.OnClickListener {
    private LensEnginePreview mPreview;
    private Button reset_btn;
    private MLFaceAnalyzer analyzer;
    private LensEngine mLensEngine;
    private int lensType = LensEngine.FRONT_LENS;
    private boolean iscameraAvailable = true;
    private final float standardPossibility = 0.95f;
    private final static int DETECT_SMILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_face);

        mPreview = findViewById(R.id.lensengine_preview);
        reset_btn = findViewById(R.id.reset_btn);
        findViewById(R.id.reset_btn).setOnClickListener(this);
        startPreview();
    }

    private void startPreview() {
        createFaceAnalyzer();
        createLensEngine();
        startLensEngine();
    }

    private MLFaceAnalyzer createFaceAnalyzer() {
        this.analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();

        this.analyzer.setTransactor(new MLAnalyzer.MLTransactor<MLFace>() {
            @Override
            public void transactResult(MLAnalyzer.Result<MLFace> result) {
                SparseArray<MLFace> faceSparseArray = result.getAnalyseList();
                int flagSmile = 0;

                for (int i = 0; i < faceSparseArray.size(); i++) {
                    MLFaceEmotion faceEmotion = faceSparseArray.valueAt(i).getEmotions();
                    if (faceEmotion.getSmilingProbability() > standardPossibility) {
                        flagSmile++;
                    }
                }
                if (flagSmile > faceSparseArray.size() * standardPossibility && iscameraAvailable) {
                    mHandler.sendEmptyMessage(DETECT_SMILE);
                }
            }

            @Override
            public void destroy() {
            }
        });

        return this.analyzer;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DETECT_SMILE:
                    Toast.makeText(ScanFaceActivity.this, "SMILING", Toast.LENGTH_SHORT).show();
                    reset_btn.setVisibility(View.VISIBLE);
                    iscameraAvailable = false;
                    break;
                default:
                    break;
            }
        }
    };

    private void createLensEngine() {
        Context context = this.getApplicationContext();
        mLensEngine = new LensEngine.Creator(context, this.analyzer)
                .setLensType(this.lensType)
                .applyDisplayDimension(1600, 1024)
                .applyFps(25.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    private void startLensEngine() {
        if (this.mLensEngine != null) {
            try {
                this.mPreview.start(this.mLensEngine);
                this.iscameraAvailable = true;
            } catch (IOException e) {
                this.mLensEngine.release();
                this.mLensEngine = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset_btn) {
            if (!iscameraAvailable) {
                iscameraAvailable = true;
                reset_btn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        if (this.analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
            }
        }
    }
}