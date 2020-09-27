package com.example.mlkitshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlkitshow.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.util.List;

/*1.define textbox and imageview.andset onclicklistener on button
  2.Click capture button and call capture photo function
  3.retrieve the image result onActivityResult
  4.create MLTextAnalyzer to recognize text in images by passing the bitmap result.(ondevide or oncloud)
  5.get return result after processing the image bitmap and display in textview
 */
public class ScanTextActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView TextView;
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE_CODE = 1;
    private MLTextAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_text);
        TextView = this.findViewById(R.id.text_result);
        imageView = this.findViewById(R.id.image_view);
        findViewById(R.id.btn_capture).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_capture) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            remoteAnalyzer(imageBitmap);
        }
    }

    private void remoteAnalyzer(Bitmap bitmaps) {
        this.analyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer();
        MLFrame frame = MLFrame.fromBitmap(bitmaps);
        Task<MLText> task = this.analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                displaySuccess(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void displaySuccess(MLText mlTexts) {
        String result = "";
        List<MLText.Block> blocks = mlTexts.getBlocks();
        for (MLText.Block block : blocks) {
            List<MLText.TextLine> lines = block.getContents();
            for (MLText.TextLine line : lines) {
                List<MLText.Word> words = line.getContents();
                for (MLText.Word word : words) {
                    result += word.getStringValue() + " ";
                }
                result += "\n";
            }
        }
        this.TextView.setText(result);
    }
}