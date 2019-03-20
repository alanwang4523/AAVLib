package com.alanwang.aavlib.libmediacore.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.extractor.AWMediaExtractor;
import com.alanwang.aavlib.libmediacore.listener.AWVoidResultListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 支持解码的格式同 MediaCodec
 * Author: AlanWang4523.
 * Date: 19/3/8 00:31.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWAudioHWDecoder {

    private static final int DECODER_TIMEOUT_IN_MS = 500;
    private MediaCodec mMediaDecoder;
    private ByteBuffer[] mInputBuffers, mOutputBuffers;
    private MediaCodec.BufferInfo mDecodeBufferInfo;
    private AWVoidResultListener mProcessListener;
    private long mPresentationTimeUs;
    private byte[] dataArr = new byte[10 * 1024];

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
    public void setProcessListener(AWVoidResultListener extractorListener) {
        this.mProcessListener = extractorListener;
        mMediaExtractor.setProcessListener(mProcessListener);
    }

    /**
     * 开始抽取数据
     */
    public void start() {
        mMediaExtractor.start();
    }

    /**
     * 停止抽取数据
     */
    public void cancel() {
        mMediaExtractor.cancel();
    }

    /**
     * 解码好的数据到来
     * @param data
     * @param offset
     * @param len
     */
    protected abstract void onDecodedAvailable(byte[] data, int offset, int len);

    /**
     * mediaFormat 已确认
     * @param mediaFormat
     */
    protected void onMediaFormatConfirmed(MediaFormat mediaFormat) {}

    protected void onRelease() {
        mMediaDecoder.stop();
        mMediaDecoder.release();
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

            // 编码数据
            int inputBufIndex = mMediaDecoder.dequeueInputBuffer(DECODER_TIMEOUT_IN_MS);
            if (inputBufIndex > 0) {
                ByteBuffer inputBuffer = mInputBuffers[inputBufIndex];
                inputBuffer.clear();

                inputBuffer.put(extractBuffer);

                mMediaDecoder.queueInputBuffer(inputBufIndex, 0, bufferInfo.size, (long) mPresentationTimeUs, 0);
                mPresentationTimeUs = bufferInfo.presentationTimeUs;
            }

            // 处理编码好的数据
            handleDecodedData();
        }

        @Override
        protected void onMediaFormatConfirmed(MediaFormat mediaFormat) throws IllegalArgumentException, IOException {
            super.onMediaFormatConfirmed(mediaFormat);
            //创建解码器
            mMediaDecoder = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
            mMediaDecoder.configure(mediaFormat, null, null, 0);
            AWAudioHWDecoder.this.onMediaFormatConfirmed(mediaFormat);
        }

        @Override
        protected void onRunPost() {
            super.onRunPost();
            int inputBufIndex = mMediaDecoder.dequeueInputBuffer(DECODER_TIMEOUT_IN_MS);
            if (inputBufIndex > 0) {
                // 当结束时需要明确发个END_OF_STREAM的标识
                mMediaDecoder.queueInputBuffer(inputBufIndex, 0, 0,
                        (long) mPresentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
            onRelease();
        }

        private void handleDecodedData() {
            int outputBufIndex = 0;
            while (outputBufIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {

                outputBufIndex = mMediaDecoder.dequeueOutputBuffer(mDecodeBufferInfo, DECODER_TIMEOUT_IN_MS);
                if (outputBufIndex >= 0) {
                    ByteBuffer decodedBuffer;
                    if (Build.VERSION.SDK_INT >= 21) {
                        decodedBuffer = mMediaDecoder.getOutputBuffer(outputBufIndex);
                    } else {
                        decodedBuffer = mOutputBuffers[outputBufIndex];
                    }

                    decodedBuffer.position(mDecodeBufferInfo.offset);
                    decodedBuffer.limit(mDecodeBufferInfo.offset + mDecodeBufferInfo.size);

                    if ((mDecodeBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0
                            && mDecodeBufferInfo.size != 0) {
                        mMediaDecoder.releaseOutputBuffer(outputBufIndex, false);
                    } else {
                        decodedBuffer.get(dataArr, 0, mDecodeBufferInfo.size);
                        onDecodedAvailable(dataArr, 0, mDecodeBufferInfo.size);
                        mMediaDecoder.releaseOutputBuffer(outputBufIndex, false);
                    }
                }
            }
        }
    };
}
