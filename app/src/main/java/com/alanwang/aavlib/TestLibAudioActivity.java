package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.alanwang.aavlib.libaudio.recorder.AWAudioDefaultRecorder;
import com.alanwang.aavlib.libmediacore.utils.AWWavFile;
import com.alanwang.aavlib.libutils.ALog;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/13 01:13.
 * Mail: alanwang4523@gmail.com
 */
public class TestLibAudioActivity extends AppCompatActivity implements View.OnClickListener {

    private AWAudioDefaultRecorder audioRecorder;
    private boolean isRecording = false;
    private AWWavFile wavFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_libaudio);

        TextView btn_test_record_to_wav = findViewById(R.id.btn_libaudio_record_to_wav);
        btn_test_record_to_wav.setOnClickListener(this);


        try {
            wavFile = new AWWavFile("/sdcard/Alan/video/audio_recorder.wav", 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioRecorder = new AWAudioDefaultRecorder(44100, 1);
        audioRecorder.setAudioListener(new AWAudioDefaultRecorder.AudioListener() {
            @Override
            public void onDataAvailable(byte[] data, int len) {
                try {
                    ALog.e("onDataAvailable()-->>" + len);
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
        if (!isRecording) {
            audioRecorder.start();
        } else {
            audioRecorder.stop();
        }
        isRecording = !isRecording;
    }
}
