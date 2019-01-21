package com.alanwang.aavlib.libeglcore.egl;

import android.opengl.EGLContext;
import android.util.Log;

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 00:36.
 * Mail: alanwang4523@gmail.com
 */

public class AAVEGLCoreWrapper {

    private final String TAG = AAVEGLCoreWrapper.class.getSimpleName();

    protected EGLCore mEglCore;
    protected AAVWindowSurface mWindowSurface;

    public AAVEGLCoreWrapper(EGLContext shareContext) {
        mWindowSurface = new AAVWindowSurface();
        mEglCore = new EGLCore(shareContext, EGLCore.FLAG_RECORDABLE);
    }

    /**
     * Creates an EGL surface associated with a Surface.
     * @param surface
     */
    public void createSurface(Object surface) {
        mWindowSurface.createWindowSurface(mEglCore, surface);
    }

    /**
     * Get current EGLContext
     * @return
     */
    public EGLContext getCurEGLContext() {
        return mEglCore.getEGLContext();
    }

    /**
     * Makes the EGL context current
     */
    public void makeCurrent() {
        mEglCore.makeCurrent(mWindowSurface.getEGLSurface());
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     * @return
     */
    public boolean swapBuffers() {
        boolean result = mEglCore.swapBuffers(mWindowSurface.getEGLSurface());
        if (!result) {
            Log.d(TAG, "WARNING: swapBuffers() failed");
        }
        return result;
    }

    /**
     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     */
    public void setPresentationTime(long nsecs) {
        mEglCore.setPresentationTime(mWindowSurface.getEGLSurface(), nsecs);
    }

    /**
     * Is current context valid
     * @return
     */
    public boolean isEGLContextValid() {
        return mWindowSurface.isWindowSurfaceValid() && mEglCore.isEGLContextValid();
    }

    /**
     * Destroy the window surface
     */
    public void destroyWindowSurface() {
        mWindowSurface.destroyWindowSurface(mEglCore);
    }

    /**
     * Release the EGLCore
     */
    public void release() {
        mEglCore.release();
    }
}
