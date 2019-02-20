package com.alanwang.aavlib.libmediacore.listener;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/21 00:20.
 * Mail: alanwang4523@gmail.com
 */
public interface AWExtractorListener {
    /**
     * 有数据到来
     * @param extractBuffer
     * @param bufferInfo
     */
    void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo);
}
