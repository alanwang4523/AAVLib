package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alanwang.aavlib.libaudio.recorder.AWWavRecorder;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/14 01:33.
 * Mail: alanwang4523@gmail.com
 */
public class TestAudioRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_audio_record;
    private AWWavRecorder audioRecorder;
    private boolean isRecording = false;
    private String wavFilePath = "/sdcard/Alan/video/audio_recorder.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_record);

        btn_audio_record = findViewById(R.id.btn_audio_record);
        btn_audio_record.setOnClickListener(this);

        try {
            audioRecorder = new AWWavRecorder(wavFilePath, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_audio_record:
                testRecordAudioToWav();
                break;
            default:
        }
    }

    /**
     * 测试录制音频到 wav
     */
    private void testRecordAudioToWav() {
        if (!isRecording && audioRecorder != null) {
            audioRecorder.start();
        } else {
            if (audioRecorder != null) {
                audioRecorder.stop();
            }
            Toast.makeText(this, wavFilePath, Toast.LENGTH_LONG).show();
        }
        isRecording = !isRecording;
        btn_audio_record.setText(isRecording ?
                this.getResources().getString(R.string.lib_audio_audio_record_stop) :
                this.getResources().getString(R.string.lib_audio_audio_record_start));
    }
}
