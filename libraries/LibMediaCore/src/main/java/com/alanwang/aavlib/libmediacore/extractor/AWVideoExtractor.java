package com.alanwang.aavlib.libmediacore.extractor;

import android.media.MediaCodec;
import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.listener.AWExtractorListener;

import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 00:42.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoExtractor extends AWMediaExtractor {

    private AWExtractorListener mAWExtractorListener;

    /**
     * 设置抽取数据的监听器
     * @param extractorListener
     */
    public void setExtractorListener(AWExtractorListener extractorListener) {
        this.mAWExtractorListener = extractorListener;
    }

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mAWExtractorListener != null) {
            mAWExtractorListener.onDataAvailable(extractBuffer, bufferInfo);
        }
    }
}
