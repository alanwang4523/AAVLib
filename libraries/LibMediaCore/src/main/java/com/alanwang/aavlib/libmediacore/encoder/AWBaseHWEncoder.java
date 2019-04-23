/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    protected MediaFormat mMediaFormat;
    protected MediaCodec mMediaEncoder;
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
    protected void onEncoderConfigured(){}

    /**
     * 编码器已开始
     */
    protected void onEncoderStarted(){}

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
    protected abstract void onEncodedDataAvailable(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo);

    /**
     * 初始化
     * @throws IOException
     * @throws InterruptedException
     */
    protected void setup() throws IOException, InterruptedException {
        try {
            mMediaEncoder = MediaCodec.createEncoderByType(getMimeType());
        } catch (Exception e) {
            throw e;
        }
        try {
            setupEncoder(mMediaFormat);
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
                if (mMediaEncoder != null) {
                    mMediaEncoder.stop();
                    setupEncoder(mMediaFormat);
                    return true;
                }
            } else if (codecException.isTransient()) {
                Thread.sleep(500);
                mMediaEncoder.start();
                return true;
            } else {
                if (mMediaEncoder != null) {
                    mMediaEncoder.release();
                }
                mMediaEncoder = MediaCodec.createEncoderByType(getMimeType());
                setupEncoder(mMediaFormat);
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
        mMediaEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mBufferInfo = new MediaCodec.BufferInfo();
        onEncoderConfigured();
        mMediaEncoder.start();
        onEncoderStarted();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaEncoder != null) {
            try {
                mMediaEncoder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            try {
                mMediaEncoder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMediaEncoder = null;
        }
    }
}
