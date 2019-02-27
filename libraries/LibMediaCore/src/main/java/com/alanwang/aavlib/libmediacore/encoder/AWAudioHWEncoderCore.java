package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 音频硬编码器
 * Author: AlanWang4523.
 * Date: 19/2/28 00:11.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWAudioHWEncoderCore extends AWBaseHWEncoder {

    private static final String MIME_TYPE = "audio/mp4a-latm";

    public AWAudioHWEncoderCore() {
    }

    /**
     * 初始化
     * @param sampleRate
     * @param channelCount
     * @param bitRate
     * @throws IOException
     * @throws InterruptedException
     */
    public void setup(int sampleRate, int channelCount, int bitRate) throws IOException, InterruptedException {
        mMediaFormat = MediaFormat.createAudioFormat(getMimeType(), sampleRate, channelCount);
        mMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        super.setup();
    }

    /**
     * 向 MediaCodec 放原始数据进行编码
     * @param byteBuffer
     * @param presentationTimeUs
     * @param isEndOfStream
     */
    protected void putRawDataToCodec(ByteBuffer byteBuffer, long presentationTimeUs, boolean isEndOfStream) {

    }

    @Override
    protected String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    protected void onEncoderConfigured() {
        // do nothing
    }

    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {

    }

    @Override
    protected void handleEncodedData(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {

    }
}
