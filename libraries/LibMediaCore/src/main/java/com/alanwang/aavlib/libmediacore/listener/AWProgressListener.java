package com.alanwang.aavlib.libmediacore.listener;

/**
 * Author: AlanWang4523.
 * Date: 19/3/20 23:36.
 * Mail: alanwang4523@gmail.com
 */
public interface AWProgressListener {
    /**
     * 处理的进度回调
     * @param percent
     */
    void onProgress(int percent);
}
