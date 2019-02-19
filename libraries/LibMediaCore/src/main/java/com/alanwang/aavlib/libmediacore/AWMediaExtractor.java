package com.alanwang.aavlib.libmediacore;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
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
    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mIsExtractorReady = false;
    private volatile boolean mIsRunning = false;

    public AWMediaExtractor() {
    }

    /**
     * 设置资源文件
     * @param mediaPath
     * @throws IOException
     */
    public void setDataSource(String mediaPath) throws IOException, IllegalAccessException {
        mExtractor = new MediaExtractor();
        mExtractor.setDataSource(mediaPath);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(mediaPath);

        mDurationMs = -1;
        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (!TextUtils.isEmpty(durationStr)) {
            try {
                mDurationMs = Long.parseLong(durationStr) * 1000;
            } catch (NumberFormatException ex) { }
        }

        int trackIndex = getTrackIndex(mExtractor);
        if (trackIndex < 0) {
            throw new IllegalAccessException("Cannot access the track index!");
        }
        onMediaFormatConfirmed(mExtractor.getTrackFormat(trackIndex));
        mBufferInfo = new MediaCodec.BufferInfo();
        mIsExtractorReady = true;
    }

    /**
     * 开始抽取数据
     */
    public void start() {
        if (mIsExtractorReady && !mIsRunning) {
            StringBuilder strBuilder = new StringBuilder(TAG);
            strBuilder.append("-").append(System.currentTimeMillis());

            Thread thread = new Thread(null, workRunnable, strBuilder.toString());
            thread.start();
            mIsRunning = true;
        }
    }

    /**
     * 停止抽取数据
     */
    public void stop() {
        mIsRunning = false;
    }

    /**
     * 是否是当前感兴趣的Track，有子类实现，视频对video感兴趣，音频对audio感兴趣
     * @param keyMimeString
     * @return
     */
    protected abstract boolean isTheInterestedTrack(String keyMimeString);

    /**
     * MediaFormat 已确认，子类实现该方法可以通过 MediaFormat 获取需要的信息
     * @param mediaFormat
     */
    protected abstract void onMediaFormatConfirmed(MediaFormat mediaFormat);

    /**
     * 获取接受抽取输出的数据
     * @return
     */
    protected abstract ByteBuffer getBufferForOutputData();

    /**
     * 有数据抽出的回调
     * @param extractBuffer
     */
    protected abstract void onDataAvailable(ByteBuffer extractBuffer);

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

    private void resetBufferInfo(MediaCodec.BufferInfo bufferInfo) {
        bufferInfo.offset = 0;
        bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
        bufferInfo.size = 0;
        bufferInfo.presentationTimeUs = 0L;
    }

    private void release() {
        if (mExtractor != null) {
            mExtractor.release();
            mExtractor = null;
        }
        mIsRunning = false;
        mIsExtractorReady = false;
    }

    private Runnable workRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsRunning) {
                int readedCount = 0;
                try {
                    readedCount = mExtractor.readSampleData(getBufferForOutputData(), 0);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                if (readedCount < 0) {
                    break;
                }
                resetBufferInfo(mBufferInfo);

                mBufferInfo.size = readedCount;
                mBufferInfo.offset = 0;
                mBufferInfo.flags = mExtractor.getSampleFlags();
                mBufferInfo.presentationTimeUs = mExtractor.getSampleTime();
            }
            release();
        }
    };
}
