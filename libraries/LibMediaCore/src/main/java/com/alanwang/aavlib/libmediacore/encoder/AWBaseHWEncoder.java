package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 抽象的硬编码器，音频编码器、视频编码器均继承该类
 * Author: AlanWang4523.
 * Date: 19/2/25 00:57.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWBaseHWEncoder {
    private final static String TAG = AWBaseHWEncoder.class.getSimpleName();
    protected static final int CODEC_TIMEOUT_IN_US = 0;
    private static final int MAX_EOS_SPINS = 10;
    protected MediaFormat mFormat;

    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    private ByteBuffer[] mEncoderOutputBuffers;
    private int mEosSpinCount = 0;

    /**
     * 创建
     * @return
     */
    protected abstract MediaFormat createMediaFormat();

    /**
     * 获取 MIME_TYPE,
     * 音频为：audio/mp4a-latm
     * 视频为：video/avc
     * @return
     */
    protected abstract String getMimeType();

    /**
     * 初始化完成
     */
    protected abstract void onEncoderConfigured();

    /**
     * 初始化
     * @throws IOException
     * @throws InterruptedException
     */
    protected void setup() throws IOException, InterruptedException {
        try {
            mFormat = createMediaFormat();
            mEncoder = MediaCodec.createEncoderByType(getMimeType());
            mBufferInfo = new MediaCodec.BufferInfo();
        } catch (Exception e) {
            throw e;
        }
        try {
            setupEncoder(mFormat);
        } catch (Exception e) {
            if (!retrySetupWhenFailed(e)) {
                throw e;
            }
        }
    }

    /**
     * 初始化失败时进行重试
     * @param e
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean retrySetupWhenFailed(Exception e) throws IOException, InterruptedException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        if (!(e instanceof MediaCodec.CodecException)) {
            return false;
        }
        try {
            MediaCodec.CodecException codecException = (MediaCodec.CodecException) e;
            Log.e(TAG, "isRecoverable = " + codecException.isRecoverable() + ", isTransient = " + codecException.isTransient());
            if (codecException.isRecoverable()) {
                if (mEncoder != null) {
                    mEncoder.stop();
                    setupEncoder(mFormat);
                    return true;
                }
            } else if (codecException.isTransient()) {
                Thread.sleep(500);
                mEncoder.start();
                return true;
            } else {
                if (mEncoder != null) {
                    mEncoder.release();
                }
                mEncoder = MediaCodec.createEncoderByType(getMimeType());
                setupEncoder(mFormat);
                return true;
            }
        } catch (Exception e1) {
            throw e1;
        }
        return false;
    }

    /**
     * 初始化编码器
     * @param format
     * @throws IOException
     */
    private void setupEncoder(MediaFormat format) throws IOException {
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        onEncoderConfigured();
        mEncoder.start();
    }

    /**
     * 向 MediaCodec 放原始数据进行编码
     * @param byteBuffer
     * @param presentationTimeUs
     * @param isEndOfStream
     */
    public void putRawDataToCodec(ByteBuffer byteBuffer, long presentationTimeUs, boolean isEndOfStream) {
        // TODO put data to codec
    }

    public void takeEncodedDataFromCodec() {

    }

    /**
     * 处理编码数据
     * @param endOfStream
     */
    public void drainEncoder(boolean endOfStream) {
        if (endOfStream) {
            signalEndOfInputStream();
        }

        while (true) {
            // 硬件编码器，在队列中申请一个操作对象
            final int encoderStatus;
            try {
                encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, CODEC_TIMEOUT_IN_US);
            } catch (Exception e) {
                Log.e(TAG, "dequeueOutputBuffer error", e);
                break;//状态错误退出本次循环
            }
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;
                } else {
                    mEosSpinCount++;
                    if (mEosSpinCount > MAX_EOS_SPINS) {
                        break;
                    }
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                mEncoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mEncoder.getOutputFormat();
                outputFormatChanged(newFormat);
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
            } else {
                // encodedData为真正编码完成的数据
                ByteBuffer encodedData;
                if (Build.VERSION.SDK_INT >= 21) {
                    encodedData = mEncoder.getOutputBuffer(encoderStatus);
                } else {
                    encodedData = mEncoderOutputBuffers[encoderStatus];
                }
                if (encodedData != null) {
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        mBufferInfo.size = 0;
                    }
                    if (mBufferInfo.size > 0) {
                        encodedData.position(mBufferInfo.offset);
                        encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                        handleEncodedData(encodedData, mBufferInfo);
                    }
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    }
                    break;
                }
            }
        }
    }


    public void release() {
        if (mEncoder != null) {
            try {
                mEncoder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            try {
                mEncoder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mEncoder = null;
        }
    }

    /**
     * 编码器的 outputFormat 发生改变
     * @param newFormat
     */
    protected abstract void outputFormatChanged(MediaFormat newFormat);

    /**
     * 处理编码好的数据
     * @param encodedData
     * @param bufferInfo
     */
    protected abstract void handleEncodedData(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo);

    /**
     * 发送结束编码的信号
     */
    private void signalEndOfInputStream() {
        try {
            mEncoder.signalEndOfInputStream();
        } catch (IllegalStateException e) {
            Log.e(TAG, "signalEndOfInputStream error", e);
        }
    }
}
