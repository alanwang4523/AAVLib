package com.alanwang.aavlib.libmediacore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 01:09.
 * Mail: alanwang4523@gmail.com
 */
public interface AWExtractorListener {
    /**
     * 有数据到来
     * @param codecFrame
     */
    void onDataAvailable(AWCodecFrame codecFrame);

    /**
     * 抽取结束
     */
    void onFinish();

    /**
     * 抽取错误
     */
    void onError(String error);
}
