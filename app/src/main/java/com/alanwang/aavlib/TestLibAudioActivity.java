package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Author: AlanWang4523.
 * Date: 19/3/13 01:13.
 * Mail: alanwang4523@gmail.com
 */
public class TestLibAudioActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_libaudio);

        TextView btn_test_record_to_wav = findViewById(R.id.btn_libaudio_record_to_wav);
        btn_test_record_to_wav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_libaudio_record_to_wav:
                testRecordAudioToWav();
                break;
            default:
        }
    }

    /**
     * 测试录制音频到 wav
     */
    private void testRecordAudioToWav() {
    }
}
