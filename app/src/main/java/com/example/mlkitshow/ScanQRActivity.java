package com.example.mlkitshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;


//1.define the button and textview
//2.call ScanUtil.startSca to start the default Scan design by huawei
//3.result return and browse it.

public class ScanQRActivity extends AppCompatActivity implements View.OnClickListener {
    TextView resulttxt;
    private static final int REQUEST_DEFAULT_CODE_SCAN = 0X01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r);
        findViewById(R.id.btn_default).setOnClickListener(this);
        resulttxt = findViewById(R.id.result_txt);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_default) {
            ScanUtil.startScan(ScanQRActivity.this, REQUEST_DEFAULT_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HmsScan hmsScan = new HmsScan();
        if (requestCode == REQUEST_DEFAULT_CODE_SCAN) {
            hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
        }
        if (hmsScan != null && !TextUtils.isEmpty(hmsScan.getOriginalValue())) {
            resulttxt.setText(hmsScan.getOriginalValue());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hmsScan.getOriginalValue())));

        }
    }
}