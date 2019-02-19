package com.alanwang.aavlib.libmediacore;

import android.media.MediaCodec;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 01:09.
 * Mail: alanwang4523@gmail.com
 */
public interface AWExtractorListener {
    /**
     * 有数据到来
     * @param extractBuffer
     * @param bufferInfo
     */
    void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo);

    /**
     * 抽取结束
     */
    void onFinish();

    /**
     * 抽取错误
     */
    void onError(String error);
}
