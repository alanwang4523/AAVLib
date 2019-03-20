package com.alanwang.aavlib.libmediacore.listener;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 01:09.
 * Mail: alanwang4523@gmail.com
 */
public interface AWMediaListener {

    /**
     * 处理的进度回调
     * @param percent
     */
    void onProgress(int percent);

    /**
     * 抽取结束
     */
    void onFinish();

    /**
     * 抽取错误
     */
    void onError(String error);
}
