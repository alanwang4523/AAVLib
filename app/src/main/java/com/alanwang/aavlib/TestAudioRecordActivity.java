package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aavlib.libaudio.recorder.AWWavRecorder;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/14 01:33.
 * Mail: alanwang4523@gmail.com
 */
public class TestAudioRecordActivity extends AppCompatActivity {

    private AWRecordButton btnRecord;
    private AWWavRecorder audioRecorder;
    private String wavFilePath = "/sdcard/Alan/video/audio_recorder.wav";
    private boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_record);

        btnRecord = findViewById(R.id.btn_audio_record_to_wav);
        btnRecord.setMode(AWRecordButton.Mode.MODE_SINGLE_CLICK);
        btnRecord.setListener(new AWRecordButton.OnClickListener() {
            @Override
            public void onClick() {
                mIsRecording = !mIsRecording;
                if (mIsRecording) {
                    btnRecord.setRecordStatus(AWRecordButton.Status.STATUS_RECORDING);
                    if (audioRecorder == null) {
                        audioRecorder = createARecorder();
                    }
                    if (audioRecorder != null) {
                        audioRecorder.start();
                    }
                } else {
                    btnRecord.setRecordStatus(AWRecordButton.Status.STATUS_READY);
                    if (audioRecorder != null) {
                        audioRecorder.stop();
                        audioRecorder = null;
                    }
                    Toast.makeText(TestAudioRecordActivity.this, wavFilePath, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private AWWavRecorder createARecorder() {
        AWWavRecorder audioRecorder = null;
        try {
            audioRecorder = new AWWavRecorder(wavFilePath, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioRecorder;
    }
}
