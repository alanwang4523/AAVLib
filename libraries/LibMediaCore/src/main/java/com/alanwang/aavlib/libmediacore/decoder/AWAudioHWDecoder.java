package com.alanwang.aavlib.libmediacore.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.extractor.AWMediaExtractor;
import com.alanwang.aavlib.libmediacore.listener.AWProcessListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/3/8 00:31.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioHWDecoder {

    private MediaCodec mMediaDecoder;
    private ByteBuffer[] mInputBuffers, mOutputBuffers;
    private MediaCodec.BufferInfo mDecodeBufferInfo;
    private AWProcessListener mProcessListener;
    private volatile boolean mIsRunning = false;

    /**
     * 设置资源文件
     * @param mediaPath
     * @throws IOException
     */
    public void setDataSource(String mediaPath) throws IOException, IllegalArgumentException {
        mMediaExtractor.setDataSource(mediaPath);
    }

    /**
     * 设置需要解码的起止时间，必须在 {@link #setDataSource} 之后调用
     * @param startTimeMs 单位：毫秒
     * @param endTimeMs 单位：毫秒
     */
    public void setStartEndTime(long startTimeMs, long endTimeMs) {
        mMediaExtractor.setExtractTime(startTimeMs, endTimeMs);
    }

    /**
     * 设置监听器
     * @param extractorListener
     */
    public void setProcessListener(AWProcessListener extractorListener) {
        this.mProcessListener = extractorListener;
    }

    protected AWMediaExtractor mMediaExtractor = new AWMediaExtractor() {
        @Override
        protected boolean isTheInterestedTrack(String keyMimeString) {
            if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("audio")) {
                return true;
            }
            return false;
        }

        @Override
        protected void onRunPre() {
            super.onRunPre();
            mMediaDecoder.start();
            mInputBuffers = mMediaDecoder.getInputBuffers();
            mOutputBuffers = mMediaDecoder.getOutputBuffers();
            mDecodeBufferInfo = new MediaCodec.BufferInfo();
        }

        @Override
        protected void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {

        }

        @Override
        protected void onMediaFormatConfirmed(MediaFormat mediaFormat) throws IllegalArgumentException, IOException {
            super.onMediaFormatConfirmed(mediaFormat);
            //创建解码器
            mMediaDecoder = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
            mMediaDecoder.configure(mediaFormat, null, null, 0);
        }
    };
}
