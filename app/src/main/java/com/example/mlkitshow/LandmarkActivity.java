package com.example.mlkitshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLCoordinate;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//1.define textview and imageview to show the result.define button to browse photo
//2.create an action to browse ur phone photo gallery
//3.return result after select the photo
//4.create analyzer and pass the result in bitmap format to process the result

public class LandmarkActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextView;
    private ImageView imageView;
    static final int REQUEST_CODE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTextView = this.findViewById(R.id.text_result);
        imageView = this.findViewById(R.id.image_view);
        findViewById(R.id.btn_browse).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_browse) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            LandmarkActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO) {
            final Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                RemoteLandAnalyzer(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void RemoteLandAnalyzer(final Bitmap bitmap) {
        MLRemoteLandmarkAnalyzer analyzer = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer();
        MLFrame mlFrame = new MLFrame.Creator().setBitmap(bitmap).create();
        Task<List<MLRemoteLandmark>> task = analyzer.asyncAnalyseFrame(mlFrame);
        task.addOnSuccessListener(new OnSuccessListener<List<MLRemoteLandmark>>() {
            public void onSuccess(List<MLRemoteLandmark> landmarkResults) {
                List<MLCoordinate> mlCoordinates = new ArrayList<MLCoordinate>();
                mlCoordinates.add(landmarkResults.get(0).getPositionInfos().get(0));
                mTextView.setText(landmarkResults.get(0).getLandmark() + "\n" +
                        mlCoordinates.get(0).getLat() + "  " + mlCoordinates.get(0).getLng() + "\n");
                imageView.setImageBitmap(bitmap);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    public void onFailure(Exception e) {
                        try {
                            MLException mlException = (MLException) e;
                        } catch (Exception error) {
                        }
                    }
                });
    }

}