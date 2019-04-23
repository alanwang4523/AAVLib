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
