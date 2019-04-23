/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libeglcore.egl;

import android.opengl.EGLContext;
import android.util.Log;

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 00:36.
 * Mail: alanwang4523@gmail.com
 */

public class AWEGLCoreWrapper {

    private final String TAG = AWEGLCoreWrapper.class.getSimpleName();

    protected EGLCore mEglCore;
    protected AWWindowSurface mWindowSurface;

    public AWEGLCoreWrapper(EGLContext shareContext) {
        mWindowSurface = new AWWindowSurface();
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
