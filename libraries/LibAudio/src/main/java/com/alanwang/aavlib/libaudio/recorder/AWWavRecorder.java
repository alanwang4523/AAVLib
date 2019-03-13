package com.alanwang.aavlib.libaudio.recorder;

import com.alanwang.aavlib.libmediacore.utils.AWWavFile;
import com.alanwang.aavlib.libutils.ALog;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/14 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWWavRecorder {

    private AWAudioDefaultRecorder audioRecorder;
    private AWWavFile wavFile;
    private boolean isReady = false;

    public AWWavRecorder(String wavFilePath, int sampleRate, int channelCount, int bitsPerSample) throws IOException {
        wavFile = new AWWavFile(wavFilePath, sampleRate, channelCount, bitsPerSample);
        audioRecorder = new AWAudioDefaultRecorder(sampleRate, channelCount);
        audioRecorder.setAudioListener(audioListener);
        isReady = true;
    }

    public void start() {
        if (isReady) {
            audioRecorder.start();
        }
    }

    public void stop() {
        if (audioRecorder != null) {
            audioRecorder.stop();
        }
    }

    private AWAudioDefaultRecorder.AudioListener audioListener = new AWAudioDefaultRecorder.AudioListener() {
        @Override
        public void onDataAvailable(byte[] data, int len) {
            try {
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
    };
}
