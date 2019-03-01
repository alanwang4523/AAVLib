package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;

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
    protected static final int CODEC_TIMEOUT_IN_US = 0;

    protected ByteBuffer[] mEncoderInputBuffers;
    protected ByteBuffer[] mEncoderOutputBuffers;

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
    protected void setup(int sampleRate, int channelCount, int bitRate) throws IOException, InterruptedException {
        mMediaFormat = MediaFormat.createAudioFormat(getMimeType(), sampleRate, channelCount);
        mMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        super.setup();
    }

    /**
     * 向 MediaCodec 放原始数据进行编码
     * @param byteBuffer
     * @param dataSize
     * @param presentationTimeUs
     * @param isEndOfStream
     * @return 是否有可填充数据的 buffer
     */
    protected boolean putRawDataToCodec(ByteBuffer byteBuffer, int dataSize, long presentationTimeUs, boolean isEndOfStream) {
        int inputBufIndex = mMediaEncoder.dequeueInputBuffer(CODEC_TIMEOUT_IN_US);
        if (inputBufIndex == -1) {
            return false;
        }

        ByteBuffer inputBuffer = mEncoderInputBuffers[inputBufIndex];
        inputBuffer.clear();
        inputBuffer.put(byteBuffer.array(), byteBuffer.position(), dataSize);

        int flags = isEndOfStream ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0;
        mMediaEncoder.queueInputBuffer(inputBufIndex, 0, dataSize, presentationTimeUs, flags);

        return true;
    }

    /**
     * 抽取编码好的数据
     * @return 是否可以继续处理
     */
    protected boolean extractEncodedData() {
        int outputBufIndex = mMediaEncoder.dequeueOutputBuffer(mBufferInfo, CODEC_TIMEOUT_IN_US);
        if (outputBufIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            return false;
        }
        if (outputBufIndex >= 0) {
            ByteBuffer encodedData;
            if (Build.VERSION.SDK_INT >= 21) {
                encodedData = mMediaEncoder.getOutputBuffer(outputBufIndex);
            } else {
                encodedData = mEncoderOutputBuffers[outputBufIndex];
            }
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && mBufferInfo.size != 0) {
                mMediaEncoder.releaseOutputBuffer(outputBufIndex, false);
            } else {
                onEncodedDataAvailable(encodedData, mBufferInfo);
                mMediaEncoder.releaseOutputBuffer(outputBufIndex, false);
            }
        } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            onOutputFormatChanged(mMediaEncoder.getOutputFormat());
        } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            mEncoderOutputBuffers = mMediaEncoder.getOutputBuffers();
        }
        return true;
    }

    @Override
    protected String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    protected void onEncoderStarted() {
        super.onEncoderStarted();
        mEncoderInputBuffers = mMediaEncoder.getInputBuffers();
        mEncoderOutputBuffers = mMediaEncoder.getOutputBuffers();
    }
}
