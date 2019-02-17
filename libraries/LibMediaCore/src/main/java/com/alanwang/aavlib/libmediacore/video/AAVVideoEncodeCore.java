package com.alanwang.aavlib.libmediacore.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 抽象的视频编码器，只负责编码视频，至于编码后的数据用法（如推流、保存到文件）由其子类实现
 * Author: AlanWang4523.
 * Date: 19/2/17 23:59.
 * Mail: alanwang4523@gmail.com
 */

public abstract class AAVVideoEncodeCore {
    private static final String TAG = AAVVideoEncodeCore.class.getSimpleName();

    private static final String MIME_TYPE = "video/avc";
    protected static final int FRAME_RATE = 30;
    protected static final int DEFAULT_I_FRAME_INTERVAL = 2;
    protected static final int TIMEOUT_USEC = 0;

    protected MediaFormat mFormat;

    private Surface mInputSurface;
    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;

    private int mEosSpinCount = 0;
    private final int MAX_EOS_SPINS = 10;

    public AAVVideoEncodeCore(int width, int height, int bitRate, int frameRate, int iFrameInterval) {
        mFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        mFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval);
    }

    /**
     * 初始化
     * @throws IOException
     * @throws InterruptedException
     */
    public void setup() throws IOException, InterruptedException {
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mBufferInfo = new MediaCodec.BufferInfo();
        } catch (Exception e) {
            throw e;
        }
        try {
            setupEncoder(mFormat);
        } catch (Exception e) {
            Log.e(TAG, "" + e);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (e instanceof MediaCodec.CodecException) {
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
                        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
                        setupEncoder(mFormat);
                        return true;
                    }
                }
            } catch (Exception e1) {
                throw e1;
            }
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
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();
    }

    /**
     * 获取编码器的 InputSurface
     * @return
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    /**
     * 处理编码数据
     * @param endOfStream
     */
    public void drainEncoder(boolean endOfStream) {
        signalEndOfInputStream(endOfStream);

        ByteBuffer[] encoderOutputBuffers = null;
        try {
            encoderOutputBuffers = mEncoder.getOutputBuffers();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;//如果状态异常抛弃该次处理
        }

        while (true) {
            // 硬件编码器，在队列中申请一个操作对象
            final int encoderStatus;
            try {
                encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
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
                encoderOutputBuffers = mEncoder.getOutputBuffers();
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
                    encodedData = encoderOutputBuffers[encoderStatus];
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

    /**
     * 释放资源
     */
    public void release() {
        if (mEncoder != null) {
            try {
                mEncoder.stop();
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
     * 发送编码结束信号
     * @param endOfStream
     */
    private void signalEndOfInputStream(boolean endOfStream) {
        if (endOfStream) {
            try {
                mEncoder.signalEndOfInputStream();
            } catch (IllegalStateException e) {
                Log.e(TAG, "signalEndOfInputStream error", e);
            }
        }
    }
}
