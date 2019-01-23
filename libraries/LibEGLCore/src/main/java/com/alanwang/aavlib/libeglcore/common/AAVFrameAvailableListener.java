package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/23 23:48.
 * Mail: alanwang4523@gmail.com
 */

public interface AAVFrameAvailableListener {
    /**
     * 通知新的一帧数据到来
     * @param surfaceTexture
     */
    void onFrameAvailable(AAVSurfaceTexture surfaceTexture);
}
