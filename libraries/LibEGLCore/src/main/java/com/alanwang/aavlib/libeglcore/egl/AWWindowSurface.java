package com.alanwang.aavlib.libeglcore.egl;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 00:34.
 * Mail: alanwang4523@gmail.com
 */

public class AWWindowSurface {

    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

    public void createWindowSurface(EGLCore eglCore, Object surface) {
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
//            throw new IllegalStateException("surface already created");
            eglCore.makeNothingCurrent();
            eglCore.destroyEGLSurface(mEGLSurface);
            mEGLSurface = EGL14.EGL_NO_SURFACE;
        }
        mEGLSurface = eglCore.createWindowSurface(surface);
    }

    public EGLSurface getEGLSurface() {
        return this.mEGLSurface;
    }

    public void destroyWindowSurface(EGLCore eglCore) {
        eglCore.releaseSurface(mEGLSurface);
        mEGLSurface = EGL14.EGL_NO_SURFACE;
    }

    public boolean isWindowSurfaceValid() {
        return mEGLSurface != EGL14.EGL_NO_SURFACE;
    }
}
