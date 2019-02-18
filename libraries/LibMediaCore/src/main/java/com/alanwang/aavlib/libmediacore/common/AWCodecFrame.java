package com.alanwang.aavlib.libmediacore.common;

import android.media.MediaCodec;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 00:56.
 * Mail: alanwang4523@gmail.com
 */
public class AWCodecFrame {
    // buffer
    private ByteBuffer mBuffer;
    // bufferInfo
    private MediaCodec.BufferInfo mBufferInfo;

    public AWCodecFrame(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        this.mBuffer = buffer;
        this.mBufferInfo = bufferInfo;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public void setBuffer(ByteBuffer mBuffer) {
        this.mBuffer = mBuffer;
    }

    public MediaCodec.BufferInfo getBufferInfo() {
        return mBufferInfo;
    }

    public void setBufferInfo(MediaCodec.BufferInfo mBufferInfo) {
        this.mBufferInfo = mBufferInfo;
    }
}
