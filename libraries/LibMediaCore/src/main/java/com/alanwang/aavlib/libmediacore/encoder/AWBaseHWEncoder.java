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

    protected MediaFormat mFormat;
    protected MediaCodec mEncoder;
    protected MediaCodec.BufferInfo mBufferInfo;

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
     * 编码器的 outputFormat 发生改变
     * @param newFormat
     */
    protected abstract void onOutputFormatChanged(MediaFormat newFormat);

    /**
     * 处理编码好的数据
     * @param encodedData
     * @param bufferInfo
     */
    protected abstract void handleEncodedData(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo);

    /**
     * 初始化
     * @throws IOException
     * @throws InterruptedException
     */
    protected void setup() throws IOException, InterruptedException {
        try {
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
     * 释放资源
     */
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
}
