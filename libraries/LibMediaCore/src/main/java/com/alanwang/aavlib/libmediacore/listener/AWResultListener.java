package com.alanwang.aavlib.libmediacore.listener;

import com.alanwang.aavlib.libmediacore.exception.AWException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/15 13:18.
 * Mail: alanwang4523@gmail.com
 */
public interface AWResultListener<T> {
    /**
     * 成功的回调
     * @param result
     */
    void onSuccess(T result);

    /**
     * 错误回调，可通过 e 获取错误码和错误信息
     */
    void onError(AWException e);
}
