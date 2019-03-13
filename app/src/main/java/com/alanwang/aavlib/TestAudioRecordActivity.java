package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alanwang.aavlib.libaudio.recorder.AWAudioDefaultRecorder;
import com.alanwang.aavlib.libmediacore.utils.AWWavFile;
import com.alanwang.aavlib.libutils.ALog;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/14 01:33.
 * Mail: alanwang4523@gmail.com
 */
public class TestAudioRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_audio_record;
    private AWAudioDefaultRecorder audioRecorder;
    private boolean isRecording = false;
    private AWWavFile wavFile;
    private String wavFilePath = "/sdcard/Alan/video/audio_recorder.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_record);

        btn_audio_record = findViewById(R.id.btn_audio_record);
        btn_audio_record.setOnClickListener(this);

        initRecorder();
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

    private void initRecorder() {
        try {
            wavFile = new AWWavFile(wavFilePath, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioRecorder = new AWAudioDefaultRecorder(44100, 1);
        audioRecorder.setAudioListener(new AWAudioDefaultRecorder.AudioListener() {
            @Override
            public void onDataAvailable(byte[] data, int len) {
                try {
                    ALog.e("onDataAvailable()-->>" + len);
                    // 可在此处做音效处理
                    wavFile.writeData(data, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errMsg) {
                ALog.e("onError()-->>" + errMsg);
            }

            @Override
            public void onFinish() {
                ALog.e("onFinish()-->>");
                try {
                    wavFile.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 测试录制音频到 wav
     */
    private void testRecordAudioToWav() {
        if (!isRecording) {
            audioRecorder.start();
        } else {
            audioRecorder.stop();
            Toast.makeText(this, wavFilePath, Toast.LENGTH_LONG).show();
        }
        isRecording = !isRecording;
        btn_audio_record.setText(isRecording ?
                this.getResources().getString(R.string.lib_audio_audio_record_stop) :
                this.getResources().getString(R.string.lib_audio_audio_record_start));
    }
}
