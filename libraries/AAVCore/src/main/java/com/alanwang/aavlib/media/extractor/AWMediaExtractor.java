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
package com.alanwang.aavlib.media.extractor;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.alanwang.aavlib.media.exception.AWMediaException;
import com.alanwang.aavlib.media.listener.AWVoidResultListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 01:08.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWMediaExtractor {
    private final static String TAG = AWMediaExtractor.class.getSimpleName();
    protected MediaExtractor mExtractor = null;
    protected long mDurationMs = -1;

    private int mTrackIndex = -1;
    private ByteBuffer mByteBuffer;
    private MediaCodec.BufferInfo mBufferInfo;
    private AWVoidResultListener mProcessListener;
    private long mStartPosTimeUs = 0; // 需要抽取的起始时间，单位微秒
    private long mEndPosTimeUs = 0; // 需要抽取的截止时间，单位微秒
    private boolean mIsExtractorReady = false;
    private volatile boolean mIsRunning = false;

    public AWMediaExtractor() {
    }

    /**
     * 设置资源文件
     * @param mediaPath
     * @throws IOException
     */
    public void setDataSource(String mediaPath) throws IOException, IllegalArgumentException {
        mExtractor = new MediaExtractor();
        mExtractor.setDataSource(mediaPath);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(mediaPath);

        mDurationMs = -1;
        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (!TextUtils.isEmpty(durationStr)) {
            try {
                mDurationMs = Long.parseLong(durationStr);
            } catch (NumberFormatException ex) { }
        }

        mTrackIndex = getTrackIndex(mExtractor);
        if (mTrackIndex < 0) {
            throw new IllegalArgumentException("Cannot access the track index!");
        }
        mExtractor.selectTrack(mTrackIndex);

        onMediaFormatConfirmed(mExtractor.getTrackFormat(mTrackIndex));
        mBufferInfo = new MediaCodec.BufferInfo();

        mStartPosTimeUs = 0;
        mEndPosTimeUs = mDurationMs * 1000;
        mIsExtractorReady = true;
    }

    /**
     * 设置需要抽取的起止时间，必须在 {@link #setDataSource} 之后调用
     * @param startTimeMs 单位：毫秒
     * @param endTimeMs 单位：毫秒
     */
    public void setExtractTime(long startTimeMs, long endTimeMs) {
        if (startTimeMs < 0 || endTimeMs < 0 || startTimeMs >= endTimeMs) {
            throw new IllegalArgumentException("The times is invalid!");
        }
        if (!mIsExtractorReady) {
            throw new IllegalArgumentException("Must be called after call setDataSource successfully!");
        }
        mStartPosTimeUs = startTimeMs * 1000;
        mEndPosTimeUs = endTimeMs * 1000;
        mExtractor.seekTo(mStartPosTimeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
    }

    /**
     * 设置监听器
     * @param extractorListener
     */
    public void setProcessListener(AWVoidResultListener extractorListener) {
        this.mProcessListener = extractorListener;
    }

    /**
     * 获取文件时长，必须在 {@link #setDataSource} 之后调用
     * @return
     */
    public long getDurationMs() {
        return mDurationMs;
    }

    /**
     * 获取 MediaFormat
     * @return
     */
    public MediaFormat getMediaFormat() {
        if (!mIsExtractorReady) {
            throw new IllegalStateException("Must be called after call setDataSource successfully!");
        }
        return mExtractor.getTrackFormat(mTrackIndex);
    }

    /**
     * 开始抽取数据
     */
    public void start() {
        if (mIsExtractorReady && !mIsRunning) {
            StringBuilder strBuilder = new StringBuilder(TAG);
            strBuilder.append("-").append(System.currentTimeMillis());

            Thread thread = new Thread(null, workRunnable, strBuilder.toString());
            mIsRunning = true;
            thread.start();
        }
    }

    /**
     * 停止抽取数据
     */
    public void cancel() {
        mIsRunning = false;
    }

    /**
     * 是否是当前感兴趣的Track，有子类实现，视频对video感兴趣，音频对audio感兴趣
     * @param keyMimeString
     * @return
     */
    protected abstract boolean isTheInterestedTrack(String keyMimeString);

    /**
     * MediaFormat 已确认，子类继承该方法可以通过 MediaFormat 获取需要的信息
     * @param mediaFormat
     */
    protected void onMediaFormatConfirmed(MediaFormat mediaFormat) throws IllegalArgumentException, IOException {
        int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        mByteBuffer = ByteBuffer.allocate(maxInputSize);
    }

    /**
     * 获取接受抽取输出的数据
     * @return
     */
    protected ByteBuffer getBufferForOutputData() {
        return mByteBuffer;
    }

    /**
     * 有数据抽出的回调
     * @param extractBuffer
     * @param bufferInfo
     */
    protected abstract void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo);

    /**
     * 获取 track index, 此处视频和音频获取 index 的方法不一样，需要子类去实现
     * @return
     */
    protected int getTrackIndex(MediaExtractor extractor) {
        int trackIndex = -1;
        if (extractor != null) {
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                if (format.containsKey(MediaFormat.KEY_MIME)) {
                    if (isTheInterestedTrack(format.getString(MediaFormat.KEY_MIME))) {
                        trackIndex = i;
                        break;
                    }
                }
            }
        }
        return trackIndex;
    }

    protected void release() {
        if (mExtractor != null) {
            mExtractor.release();
            mExtractor = null;
        }
        mIsExtractorReady = false;
    }

    /**
     * 在 run 之前回调，用于子类做准备工作
     */
    protected void onRunPre() {}

    /**
     * 在 run 结束后回调，用于子类释放资源
     */
    protected void onRunPost() {}

    private final Runnable workRunnable = new Runnable() {
        @Override
        public void run() {
            onRunPre();
            boolean isSuccess = false;
            int readCount;
            while (mIsRunning) {
                ByteBuffer byteBuffer = getBufferForOutputData();
                if (byteBuffer == null && mProcessListener != null) {
                    mProcessListener.onError(new AWMediaException("Buffer cannot be null！"));
                    break;
                }
                byteBuffer.clear();

                try {
                    readCount = mExtractor.readSampleData(byteBuffer, 0);
                } catch (IllegalArgumentException e) {
                    if (mProcessListener != null) {
                        mProcessListener.onError(new AWMediaException("Buffer not enough！"));
                    }
                    break;
                }

                if (readCount != -1) {
                    mBufferInfo.size = readCount;
                    mBufferInfo.offset = 0;
                    mBufferInfo.flags = mExtractor.getSampleFlags();
                    mBufferInfo.presentationTimeUs = mExtractor.getSampleTime();

                    onDataAvailable(byteBuffer, mBufferInfo);

                    if (mProcessListener != null) {
                        mProcessListener.onProgress((int) (100.0f *
                                (mBufferInfo.presentationTimeUs - mStartPosTimeUs) /
                                (mEndPosTimeUs - mStartPosTimeUs)));
                    }
                } else {
                    isSuccess = true;
                    break;
                }

                if (mBufferInfo.presentationTimeUs >= mEndPosTimeUs) {
                    isSuccess = true;
                    break;
                }
                mExtractor.advance();
            }
            release();
            onRunPost();
            if (!mIsRunning) {
                if (mProcessListener != null) {
                    mProcessListener.onCanceled();
                }
                mIsRunning = false;
                return;
            }
            if (isSuccess) {
                if (mProcessListener != null) {
                    mProcessListener.onProgress(100);
                    mProcessListener.onSuccess(null);
                }
            }
            mIsRunning = false;
        }
    };
}
