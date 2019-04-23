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

public abstract class AWVideoHWEncoderCore extends AWBaseHWEncoder {
    private static final String TAG = AWVideoHWEncoderCore.class.getSimpleName();

    private static final String MIME_TYPE = "video/avc";
    protected static final int FRAME_RATE = 30;
    protected static final int DEFAULT_I_FRAME_INTERVAL = 2;
    protected static final int CODEC_TIMEOUT_IN_US = 0;


    private Surface mInputSurface;
    protected ByteBuffer[] mEncoderOutputBuffers;
    private int mEosSpinCount = 0;
    private final int MAX_EOS_SPINS = 10;

    public AWVideoHWEncoderCore() {
    }

    public void setup(int width, int height, int bitRate, int frameRate, int iFrameInterval) throws IOException, InterruptedException {
        mMediaFormat = MediaFormat.createVideoFormat(getMimeType(), width, height);
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval);
        super.setup();
    }

    @Override
    protected String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    protected void onEncoderConfigured() {
        super.onEncoderConfigured();
        mInputSurface = mMediaEncoder.createInputSurface();
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

        while (true) {
            // 硬件编码器，在队列中申请一个操作对象
            final int encoderStatus;
            try {
                encoderStatus = mMediaEncoder.dequeueOutputBuffer(mBufferInfo, CODEC_TIMEOUT_IN_US);
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
                mEncoderOutputBuffers = mMediaEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mMediaEncoder.getOutputFormat();
                onOutputFormatChanged(newFormat);
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
            } else {
                // encodedData为真正编码完成的数据
                ByteBuffer encodedData;
                if (Build.VERSION.SDK_INT >= 21) {
                    encodedData = mMediaEncoder.getOutputBuffer(encoderStatus);
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

                        onEncodedDataAvailable(encodedData, mBufferInfo);
                    }
                }
                mMediaEncoder.releaseOutputBuffer(encoderStatus, false);

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
     * 发送编码结束信号
     * @param endOfStream
     */
    private void signalEndOfInputStream(boolean endOfStream) {
        if (endOfStream) {
            try {
                mMediaEncoder.signalEndOfInputStream();
            } catch (IllegalStateException e) {
                Log.e(TAG, "signalEndOfInputStream error", e);
            }
        }
    }
}
