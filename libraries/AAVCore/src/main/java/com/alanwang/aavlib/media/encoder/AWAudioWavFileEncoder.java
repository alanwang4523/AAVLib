/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.media.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import com.alanwang.aavlib.media.exception.AWMediaException;
import com.alanwang.aavlib.media.listener.AWVoidResultListener;
import com.alanwang.aavlib.media.utils.AWWavFileHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/25 00:52.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioWavFileEncoder extends AWAudioHWEncoderCore {
    private static final String TAG = AWAudioWavFileEncoder.class.getSimpleName();
    private FileInputStream mWavInputStream = null;
    private AWVoidResultListener mProcessListener;
    private MediaMuxer mMediaMuxer;
    private byte[] mTempBuffer;
    private int mAudioTrackIdx = 0;
    private int mSampleRate;
    private int mChannelCount;
    private int mBytePerSample;

    private long mPresentationTimeUs;
    private long mTotalNeedProcessLen;
    private long mTotalHaveReadLen = 0;
    private boolean mIsReady = false;
    private volatile boolean mIsRunning = false;

    /**
     * 设置资源文件
     * @param wavFilePath
     * @param outputFilePath
     * @throws IOException
     */
    public void setDataSource(String wavFilePath, String outputFilePath) throws AWMediaException {
        File wavFile = new File(wavFilePath);
        if (!wavFile.exists()) {
            throw new AWMediaException("Wav file is not found!");
        }

        // 获取音频信息
        AWWavFileHelper.WavHeaderInfo headerInfo = null;
        try {
            headerInfo = AWWavFileHelper.getWavHeaderInfo(wavFile);
        } catch (IOException e) {
            throw new AWMediaException("Get wav header info failed!", e);
        }
        mSampleRate = headerInfo.sampleRate;
        mChannelCount = headerInfo.channelCount;
        mBytePerSample = headerInfo.bytePerSample;
        mTotalNeedProcessLen = headerInfo.audioDataLen;

        try {
            mMediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            throw new AWMediaException("Create MediaMuxer failed!", e);
        }
        try {
            mWavInputStream = new FileInputStream(wavFile);
            mWavInputStream.skip(44);//跳过wav文件头，从00H~2BH
        } catch (FileNotFoundException e) {
            throw new AWMediaException("The wav file not found!", e);
        } catch (IOException e) {
            throw new AWMediaException("Input stream skip failed!", e);
        }
        mTempBuffer = new byte[10 * 1024];

        mIsReady = true;
    }

    /**
     * 设置要编码的区间, 须在 {@link #setDataSource} 之后调
     * @param startTimeMs
     * @param endTimeMs 为 -1 时表示编码到文件尾
     */
    public void setEncodeTime(long startTimeMs, long endTimeMs) throws AWMediaException {
        checkIsReady();
        if (endTimeMs != -1 && startTimeMs >= endTimeMs) {
            throw new AWMediaException("Illegal Arguments: start time could not be larger than end time!");
        }
        long needSkipLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, startTimeMs);
        if (endTimeMs != -1) {
            long needEncodeLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, (endTimeMs - startTimeMs));
            mTotalNeedProcessLen = Math.min(needEncodeLen, mTotalNeedProcessLen);
        }
        try {
            mWavInputStream.skip(needSkipLen);
        } catch (IOException e) {
            throw new AWMediaException("skip failed!", e);
        }
    }

    /**
     * 必须在 {@link #setDataSource} 之后调
     * @param bitRate
     * @throws IOException
     * @throws InterruptedException
     */
    public void setup(int bitRate) throws AWMediaException {
        checkIsReady();
        try {
            super.setup(mSampleRate, mChannelCount, bitRate);
        } catch (IOException e) {
            throw new AWMediaException("Set up failed!", e);
        } catch (InterruptedException e) {
            throw new AWMediaException("Set up failed!", e);
        }
    }

    /**
     * 设置监听器
     * @param extractorListener
     */
    public void setProcessListener(AWVoidResultListener extractorListener) {
        this.mProcessListener = extractorListener;
    }

    /**
     * 开始裁剪
     */
    public void start() {
        checkIsReady();
        if (!mIsRunning) {
            StringBuilder strBuilder = new StringBuilder(TAG);
            strBuilder.append("-").append(System.currentTimeMillis());

            Thread thread = new Thread(null, workRunnable, strBuilder.toString());
            mIsRunning = true;
            thread.start();
        }
    }

    /**
     * 停止/取消裁剪
     */
    public void cancel() {
        mIsRunning = false;
    }

    @Override
    public void release() {
        super.release();
        if (mMediaMuxer != null) {
            try {
                mMediaMuxer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mMediaMuxer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final Runnable workRunnable = new Runnable() {
        @Override
        public void run() {
            boolean isSuccess = false;
            boolean isContinue;
            do {
                isContinue = fillingRawData();
                if (!isContinue) {
                    break;
                }

                isContinue = extractHaveEncodedData();
                if (!isContinue) {
                    break;
                }

                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    isSuccess = true;
                    break;
                }
            } while (mIsRunning);

            release();
            if (isSuccess) {

                if (mProcessListener != null) {
                    mProcessListener.onProgress(100);
                    mProcessListener.onSuccess(null);
                }
            }
        }
    };

    /**
     * 向编码器中填充裸数据
     * @return
     */
    private boolean fillingRawData() {
        int inputBufIndex;
        int bytesRead;
        while (mIsRunning) {
            inputBufIndex = mMediaEncoder.dequeueInputBuffer(CODEC_TIMEOUT_IN_US);
            if (inputBufIndex < 0) {
                break;
            }
            ByteBuffer inputBuffer = mEncoderInputBuffers[inputBufIndex];
            inputBuffer.clear();

            try {
                bytesRead = mWavInputStream.read(mTempBuffer, 0, inputBuffer.limit());
            } catch (IOException e) {
                if (mProcessListener != null) {
                    mProcessListener.onError(new AWMediaException("Read file error!"));
                }
                return false;
            }

            if (bytesRead == -1) {//读到文件尾，这里应该走不到才对
                mMediaEncoder.queueInputBuffer(inputBufIndex, 0, 0,
                        mPresentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                break;
            } else {
                int flags = 0;
                if (mTotalNeedProcessLen - mTotalHaveReadLen < bytesRead) {
                    bytesRead = (int)(mTotalNeedProcessLen - mTotalHaveReadLen);
                    flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                }
                mTotalHaveReadLen += bytesRead;
                inputBuffer.put(mTempBuffer, 0, bytesRead);
                mMediaEncoder.queueInputBuffer(inputBufIndex, 0, bytesRead, mPresentationTimeUs, flags);
                mPresentationTimeUs = (long) (1.0 * mTotalHaveReadLen / mBytePerSample / mSampleRate / mChannelCount * 1000 * 1000);

                if (mProcessListener != null) {
                    int percent = (int) (100.0f * mTotalHaveReadLen / mTotalNeedProcessLen);
                    mProcessListener.onProgress(percent);
                }

                if (MediaCodec.BUFFER_FLAG_END_OF_STREAM == flags) {
                    break;
                }
            }
        }
        return true;
    }

    /**
     * 抽取所有已经编码好的数据
     * @return
     */
    private boolean extractHaveEncodedData() {
        boolean isContinue;
        do {
            isContinue = extractEncodedData();
        } while (mIsRunning && isContinue);
        return true;
    }


    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {
        mAudioTrackIdx = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
    }

    @Override
    protected void onEncodedDataAvailable(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
        mMediaMuxer.writeSampleData(mAudioTrackIdx, encodedData, bufferInfo);
    }

    /**
     * 根据时长获取对应长度wav文件长度
     * @param sampleRate
     * @param channelCount
     * @param bytePerSample 每个采样点的数据长度
     * @param timeMs
     * @return
     */
    private long getLenByTime(int sampleRate, int channelCount, int bytePerSample, long timeMs) {
        int samplesNum = (int) ((timeMs / 1000.0f) * sampleRate);
        return bytePerSample * channelCount * samplesNum;
    }

    /**
     * 检测是否已经 ready
     */
    private void checkIsReady() {
        if (!mIsReady) {
            throw new IllegalStateException("Data source is not ready or ready failed!");
        }
    }
}
