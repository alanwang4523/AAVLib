package com.alanwang.aavlib.libmediacore;

import android.media.MediaCodec;
import android.media.MediaMuxer;
import com.alanwang.aavlib.libmediacore.audio.AWAudioExtractor;
import com.alanwang.aavlib.libmediacore.video.AWVideoExtractor;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 裁剪音频和视频
 * Author: AlanWang4523.
 * Date: 19/2/20 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWAVClipper {

    private AWAudioExtractor mAudioExtractor;
    private AWVideoExtractor mVideoExtractor;
    private MediaMuxer mMediaMuxer;
    private AWProcessListener mProcessListener;

    private boolean mIsHaveAudio = false;
    private boolean mIsHaveVideo = false;
    private int mAudioTrackIndex;
    private int mVideoTrackIndex;
    private volatile boolean mIsAudioProcessFinish = false;
    private volatile boolean mIsVideoProcessFinish = false;
    private int mMaxProgress = 0;
    private volatile int mAudioProgress = 0;
    private volatile int mVideoProgress = 0;
    private boolean mIsAllReady = false;
    private volatile boolean mIsStart = false;

    public AWAVClipper() {
        mAudioExtractor = new AWAudioExtractor();
        mVideoExtractor = new AWVideoExtractor();
    }

    /**
     * 设置资源文件
     * @param srcMediaPath
     * @param dstSavePath
     * @throws IOException
     */
    public void setDataSource(String srcMediaPath, String dstSavePath) throws IOException {
        mMediaMuxer = new MediaMuxer(dstSavePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        try {
            mAudioExtractor.setDataSource(srcMediaPath);
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
            mVideoExtractor.setDataSource(srcMediaPath);
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
    public void setProcessListener(AWProcessListener extractorListener) {
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
    public void stop() {
        synchronized (this) {
            if (mIsStart) {
                mIsStart = false;
                mMediaMuxer.stop();
                mMediaMuxer.release();

                if (mIsHaveAudio) {
                    mAudioExtractor.stop();
                }
                if (mIsHaveVideo) {
                    mVideoExtractor.stop();
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

    private class AVProcessListener implements AWProcessListener {
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
        public void onFinish() {
            if (isAllFinish() && mProcessListener != null) {
                mProcessListener.onFinish();
            }
        }

        @Override
        public void onError(String error) {
            if (mediaType == TYPE_AUDIO && mIsHaveVideo) {
                // 当音频出错时，将视频停止
                mVideoExtractor.stop();
            } else if (mediaType == TYPE_VIDEO && mIsHaveAudio) {
                // 当视频出错时，将音频停止
                mAudioExtractor.stop();
            }
            if (mProcessListener != null) {
                mProcessListener.onError(error);
            }
        }

        private boolean isAllFinish() {
            return mIsAudioProcessFinish && mIsVideoProcessFinish;
        }

        private void notifyProcess() {
            if (mProcessListener != null) {
                mProcessListener.onProgress((int) (100.0f * (mAudioProgress + mVideoProgress) / mMaxProgress));
            }
        }
    }

}
