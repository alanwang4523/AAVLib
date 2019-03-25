package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aav.algeneral.ui.render.AWAudioWaveView;
import com.alanwang.aavlib.libaudio.recorder.AWAudioDefaultRecorder;
import com.alanwang.aavlib.libmediacore.listener.AWDataAvailableListener;

/**
 * Author: AlanWang4523.
 * Date: 19/3/14 01:33.
 * Mail: alanwang4523@gmail.com
 */
public class TestAudioRecordActivity extends AppCompatActivity {

    private final static int MAX_SHOW_VOLUME = 50;
    private AWRecordButton btnRecord;
    private AWAudioWaveView waveVolume;
    private AWAudioDefaultRecorder audioRecorder;
    private String wavFilePath = "/sdcard/Alan/video/audio_recorder.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_record);

        waveVolume = findViewById(R.id.wave_volume);

        btnRecord = findViewById(R.id.btn_audio_record_to_wav);

        btnRecord.setMode(AWRecordButton.Mode.RECORD);
        btnRecord.setRecordListener(new AWRecordButton.OnRecordListener() {
            @Override
            public void onRecordStart() {
                if (audioRecorder == null) {
                    audioRecorder = createARecorder();
                    audioRecorder.setDataAvailableListener(audioListener);
                }
                if (audioRecorder != null) {
                    waveVolume.startWave();
                    audioRecorder.start();
                }
            }

            @Override
            public void onRecordStop() {
                if (audioRecorder != null) {
                    audioRecorder.stop();
                    waveVolume.stopWave();
                    audioRecorder = null;
                }
                Toast.makeText(TestAudioRecordActivity.this, wavFilePath, Toast.LENGTH_LONG).show();
            }
        });
    }

//    private AWWavRecorder createARecorder() {
//        AWWavRecorder audioRecorder = null;
//        try {
//            audioRecorder = new AWWavRecorder(wavFilePath, 44100, 1, 16);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return audioRecorder;
//    }

    private AWAudioDefaultRecorder createARecorder() {
        AWAudioDefaultRecorder audioRecorder = new AWAudioDefaultRecorder(44100, 1);
        return audioRecorder;
    }

    @Override
    protected void onDestroy() {
        waveVolume.stopWave();
        if (audioRecorder != null) {
            audioRecorder.stop();
        }
        super.onDestroy();
    }

    private AWDataAvailableListener audioListener = new AWDataAvailableListener() {
        @Override
        public void onDataAvailable(byte[] data, int len) {
            waveVolume.setVolume(calculateVolume(data, len));
        }
    };

    private int calculateVolume(byte[] data, int len) {
        long sumOfSquare = 0;
        for (int i = 0; i < len; i++) {
            sumOfSquare += (data[i] * data[i]);
        }
        int showVolume = (int) Math.min(Math.sqrt(sumOfSquare / len), MAX_SHOW_VOLUME);
        return (int) (100 * (showVolume * 1.0f / MAX_SHOW_VOLUME));
    }
}
