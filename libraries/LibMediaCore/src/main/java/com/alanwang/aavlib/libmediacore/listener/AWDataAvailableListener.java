package com.alanwang.aavlib.libmediacore.listener;

/**
 * Author: AlanWang4523.
 * Date: 19/3/26 00:46.
 * Mail: alanwang4523@gmail.com
 */
public interface AWDataAvailableListener {
    /**
     * 有数据到来
     * @param data
     * @param len
     */
    void onDataAvailable(byte[] data, int len);
}
