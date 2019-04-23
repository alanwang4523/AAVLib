/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libaudio.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import com.alanwang.aavlib.libmediacore.exception.AWMediaException;
import com.alanwang.aavlib.libmediacore.listener.AWDataAvailableListener;
import com.alanwang.aavlib.libmediacore.listener.AWResultListener;

/**
 * Author: AlanWang4523.
 * Date: 19/3/13 00:19.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioDefaultRecorder {
    private final static String TAG = AWAudioDefaultRecorder.class.getSimpleName();

    private AudioRecord mAudioRecorder;
    private AWResultListener<Void> mResultListener;
    private AWDataAvailableListener mDataAvailableListener;
    private byte[] mDataBuffer;
    private volatile boolean mIsRecording = false;
    private int mBufferSize;


    public AWAudioDefaultRecorder(int sampleRate, int channelCount) {
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;// 默认采样 short 型格式
        int channelConfig = channelCount == 2 ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
        mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        mDataBuffer = new byte[mBufferSize];
        mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, mBufferSize);
    }

    /**
     * 设置结果监听器
     * @param audioListener
     */
    public void setResultListener(AWResultListener<Void> audioListener) {
        this.mResultListener = audioListener;
    }

    /**
     * 设置数据监听器
     * @param dataAvailableListener
     */
    public void setDataAvailableListener(AWDataAvailableListener dataAvailableListener) {
        this.mDataAvailableListener = dataAvailableListener;
    }

    /**
     * 开始录制
     */
    public void start() {
        if (!mIsRecording) {
            StringBuilder strBuilder = new StringBuilder(TAG);
            strBuilder.append("-").append(System.currentTimeMillis());

            Thread thread = new Thread(null, workRunnable, strBuilder.toString());
            mIsRecording = true;
            thread.start();
        }
    }

    /**
     * 停止录制
     */
    public void stop() {
        mIsRecording = false;
    }

    private Runnable workRunnable = new Runnable() {
        @Override
        public void run() {
            mAudioRecorder.startRecording();
            int length;
            while (mIsRecording) {
                length = mAudioRecorder.read(mDataBuffer, 0, mBufferSize);

                if (length >= 0) {
                    if (mDataAvailableListener != null) {
                        mDataAvailableListener.onDataAvailable(mDataBuffer, length);
                    }
                } else {
                    if (mResultListener != null) {
                        mResultListener.onError(new AWMediaException("Record error : " + length));
                    }
                    break;
                }
            }
            mAudioRecorder.stop();
            mAudioRecorder.release();
            if (mResultListener != null) {
                mResultListener.onSuccess(null);
            }
        }
    };
}
