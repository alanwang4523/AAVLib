package com.alanwang.aavlib.libmediacore.muxer;

import android.media.MediaCodec;
import android.media.MediaMuxer;
import com.alanwang.aavlib.libmediacore.exception.AWException;
import com.alanwang.aavlib.libmediacore.extractor.AWAudioExtractor;
import com.alanwang.aavlib.libmediacore.extractor.AWVideoExtractor;
import com.alanwang.aavlib.libmediacore.listener.AWExtractorListener;
import com.alanwang.aavlib.libmediacore.listener.AWVoidResultListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 音视频混合器，将音频文件和视频文件合成为一个视频文件
 * Author: AlanWang4523.
 * Date: 19/2/21 01:39.
 * Mail: alanwang4523@gmail.com
 */
public class AWAVAndroidMuxer {
    private AWAudioExtractor mAudioExtractor;
    private AWVideoExtractor mVideoExtractor;
    private MediaMuxer mMediaMuxer;
    private AWVoidResultListener mProcessListener;

    private boolean mIsHaveAudio = false;
    private boolean mIsHaveVideo = false;
    private int mAudioTrackIndex;
    private int mVideoTrackIndex;
    private volatile boolean mIsAudioProcessFinish = false;
    private volatile boolean mIsVideoProcessFinish = false;
    private volatile boolean mIsAudioProcessCanceled = false;
    private volatile boolean mIsVideoProcessCanceled = false;
    private int mMaxProgress = 0;
    private volatile int mAudioProgress = 0;
    private volatile int mVideoProgress = 0;
    private boolean mIsAllReady = false;
    private volatile boolean mIsStart = false;

    public AWAVAndroidMuxer() {
        mAudioExtractor = new AWAudioExtractor();
        mVideoExtractor = new AWVideoExtractor();
    }

    /**
     * 设置资源文件
     * @param audioPath
     * @param videoPath
     * @throws IOException
     */
    public void setDataSource(String audioPath, String videoPath, String outputPath) throws IOException {
        mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        try {
            mAudioExtractor.setDataSource(audioPath);
            mAudioExtractor.setExtractorListener(mAudioDataListener);
            mAudioExtractor.setProcessListener(new AVProcessListener(AVProcessListener.TYPE_AUDIO));
            mMaxProgress += 100;
            mIsHaveAudio = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            mIsHaveAudio = false;
            mIsAudioProcessFinish = true;
        }
        if (mIsHaveAudio) {
            mAudioTrackIndex = mMediaMuxer.addTrack(mAudioExtractor.getMediaFormat());
        }

        try {
            mVideoExtractor.setDataSource(videoPath);
            mVideoExtractor.setExtractorListener(mVideoDataListener);
            mVideoExtractor.setProcessListener(new AVProcessListener(AVProcessListener.TYPE_VIDEO));
            mMaxProgress += 100;
            mIsHaveVideo = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            mIsHaveVideo = false;
            mIsVideoProcessFinish = true;
        }
        if (mIsHaveVideo) {
            mVideoTrackIndex = mMediaMuxer.addTrack(mVideoExtractor.getMediaFormat());
        }

        if (!mIsHaveAudio && !mIsHaveVideo) {
            throw new IllegalArgumentException("There is neither audio nor video!");
        }
        mIsAllReady = true;
    }


    /**
     * 设置需要抽取的起止时间，必须在 {@link #setDataSource} 之后调用
     * @param startTimeMs 单位：毫秒
     * @param endTimeMs 单位：毫秒
     */
    public void setExtractTime(long startTimeMs, long endTimeMs) {
        if (!mIsAllReady) {
            throw new IllegalStateException("Data source is not ready or ready failed!");
        }
        if (mIsHaveAudio) {
            mAudioExtractor.setExtractTime(startTimeMs, endTimeMs);
        }
        if (mIsHaveVideo) {
            mVideoExtractor.setExtractTime(startTimeMs, endTimeMs);
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
        if (!mIsAllReady) {
            throw new IllegalStateException("Data source is not ready or ready failed!");
        }
        synchronized (this) {
            mIsStart = true;
            mMediaMuxer.start();
            if (mIsHaveAudio) {
                mAudioExtractor.start();
            }
            if (mIsHaveVideo) {
                mVideoExtractor.start();
            }
        }

    }

    /**
     * 停止/取消裁剪
     */
    public void cancel() {
        synchronized (this) {
            if (mIsStart) {
                release();
                mIsStart = false;

                if (mIsHaveAudio) {
                    mAudioExtractor.cancel();
                }
                if (mIsHaveVideo) {
                    mVideoExtractor.cancel();
                }

            }
        }
    }

    /**
     * 音频抽取器的数据回调
     */
    private AWExtractorListener mAudioDataListener = new AWExtractorListener() {
        @Override
        public void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
            if (mIsStart) {
                mMediaMuxer.writeSampleData(mAudioTrackIndex, extractBuffer, bufferInfo);
            }
        }
    };

    /**
     * 视频抽取器的数据回调
     */
    private AWExtractorListener mVideoDataListener = new AWExtractorListener() {
        @Override
        public void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
            if (mIsStart) {
                mMediaMuxer.writeSampleData(mVideoTrackIndex, extractBuffer, bufferInfo);
            }
        }
    };

    /**
     * 释放资源
     */
    private void release() {
        if (mIsStart) {
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

    private class AVProcessListener implements AWVoidResultListener {
        static final int TYPE_AUDIO = 0x01;
        static final int TYPE_VIDEO = 0x02;

        private int mediaType;

        public AVProcessListener(int mediaType) {
            this.mediaType = mediaType;
        }

        @Override
        public void onProgress(int percent) {
            if (mediaType == TYPE_AUDIO) {
                mAudioProgress = percent;
            } else if (mediaType == TYPE_VIDEO) {
                mVideoProgress = percent;
            }
            notifyProcess();
        }

        @Override
        public void onSuccess(Void result) {
            if (mediaType == TYPE_AUDIO) {
                mIsAudioProcessFinish = true;
            } else if (mediaType == TYPE_VIDEO) {
                mIsVideoProcessFinish = true;
            }
            if (isAllFinish()) {
                release();
                if (mProcessListener != null) {
                    mProcessListener.onSuccess(result);
                }
            }
        }

        @Override
        public void onError(AWException e) {
            if (mediaType == TYPE_AUDIO && mIsHaveVideo) {
                // 当音频出错时，将视频停止
                mVideoExtractor.cancel();
            } else if (mediaType == TYPE_VIDEO && mIsHaveAudio) {
                // 当视频出错时，将音频停止
                mAudioExtractor.cancel();
            }
            release();
            if (mProcessListener != null) {
                mProcessListener.onError(e);
            }
        }

        @Override
        public void onCanceled() {
            if (mediaType == TYPE_AUDIO) {
                mIsAudioProcessCanceled = true;
            } else if (mediaType == TYPE_VIDEO) {
                mIsVideoProcessCanceled = true;
            }

            if (isAllCanceled()) {
                release();
                if (mProcessListener != null) {
                    mProcessListener.onCanceled();
                }
            }
        }

        private boolean isAllFinish() {
            return mIsAudioProcessFinish && mIsVideoProcessFinish;
        }

        private boolean isAllCanceled() {
            return mIsAudioProcessCanceled && mIsVideoProcessCanceled;
        }

        private void notifyProcess() {
            if (mProcessListener != null) {
                mProcessListener.onProgress((int) (100.0f * (mAudioProgress + mVideoProgress) / mMaxProgress));
            }
        }
    }

}
