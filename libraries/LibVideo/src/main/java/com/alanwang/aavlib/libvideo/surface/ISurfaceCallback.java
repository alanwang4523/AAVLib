package com.alanwang.aavlib.libvideo.surface;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 22:57.
 * Mail: alanwang4523@gmail.com
 */

public interface ISurfaceCallback {
    /**
     * onSurfaceChanged
     * @param surface surfaceHolder
     * @param w width
     * @param h height
     */
    void onSurfaceChanged(Object surface, int w, int h);

    /**
     * surfaceDestroyed
     * @param surface surfaceHolder
     */
    void onSurfaceDestroyed(Object surface);
}
