package com.alanwang.aavlib.libaudio.recorder;

import com.alanwang.aavlib.libmediacore.exception.AWException;
import com.alanwang.aavlib.libmediacore.listener.AWDataAvailableListener;
import com.alanwang.aavlib.libmediacore.listener.AWResultListener;
import com.alanwang.aavlib.libmediacore.utils.AWWavFile;
import com.alanwang.aavlib.libutils.ALog;
import java.io.IOException;

/**
 * 录制音频并写入 wav 文件
 * Author: AlanWang4523.
 * Date: 19/3/14 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWWavRecorder {

    private AWAudioDefaultRecorder audioRecorder;
    private AWWavFile wavFile;
    private boolean isReady;

    public AWWavRecorder(String wavFilePath, int sampleRate, int channelCount, int bitsPerSample) throws IOException {
        wavFile = new AWWavFile(wavFilePath, sampleRate, channelCount, bitsPerSample);
        audioRecorder = new AWAudioDefaultRecorder(sampleRate, channelCount);
        audioRecorder.setResultListener(audioListener);
        audioRecorder.setDataAvailableListener(dataAvailableListener);
        isReady = true;
    }

    /**
     * 开始录制
     */
    public void start() {
        if (isReady) {
            audioRecorder.start();
        }
    }

    /**
     * 停止录制
     */
    public void stop() {
        if (audioRecorder != null) {
            audioRecorder.stop();
        }
    }

    private AWDataAvailableListener dataAvailableListener = new AWDataAvailableListener() {
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
    };

    private AWResultListener<Void> audioListener = new AWResultListener<Void>() {

        @Override
        public void onSuccess(Void result) {
            ALog.e("onFinish()-->>");
            try {
                wavFile.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(AWException e) {
            ALog.e("onError()-->>" + e);
        }
    };
}
