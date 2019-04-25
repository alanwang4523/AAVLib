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
package com.alanwang.aavlib.video.opengl.common;

import android.opengl.GLES20;

import com.alanwang.aavlib.video.opengl.egl.GlUtil;

/**
 * 自带 texture 的 AWFrameBuffer
 * Author: AlanWang4523.
 * Date: 19/1/22 00:58.
 * Mail: alanwang4523@gmail.com
 */

public class AWFrameBuffer {

    private AWOnlyFrameBuffer mFrameBuffer;
    private long mLastThreadID = 0;
    private int mOutputTextureId = -1;
    private int mWidth = 0;
    private int mHeight = 0;

    public AWFrameBuffer() {
        mFrameBuffer = new AWOnlyFrameBuffer();
    }

    public boolean checkInit(int width, int height) {
        long id = Thread.currentThread().getId();
        if (mLastThreadID != id && mLastThreadID > 0) {
            release();
        } else if (mWidth == width && mHeight == height && mOutputTextureId != -1) {
            return true;
        } else {
            release();
        }
        mLastThreadID = id;
        return createAndBindTexture(width, height);
    }

    /**
     * 创建 texture 并绑定到 FrameBuffer
     * @param width
     * @param height
     * @return
     */
    private boolean createAndBindTexture(int width, int height) {
        mFrameBuffer.checkInit();
        mFrameBuffer.bindFrameBuffer();
        mOutputTextureId = GlUtil.create2DTexture(width, height);
        if (mOutputTextureId >= 0) {
            mFrameBuffer.bindTexture2FBO(mOutputTextureId);
        }
        mFrameBuffer.unbindFrameBuffer();
        mWidth = width;
        mHeight = height;
        return (mOutputTextureId >= 0);
    }

    /**
     * 绑定 FrameBuffer
     */
    public void bindFrameBuffer() {
        mFrameBuffer.bindFrameBuffer();
    }

    /**
     * 解绑 FrameBuffer
     */
    public void unbindFrameBuffer() {
        mFrameBuffer.unbindFrameBuffer();
    }

    /**
     * 获取 FrameBuffer 的输出 Texture
     * @return
     */
    public int getOutputTextureId() {
        return mOutputTextureId;
    }

    /**
     * 释放 FrameBuffer
     */
    public void release() {
        mFrameBuffer.release();
        if (mOutputTextureId != -1) {
            GLES20.glDeleteTextures(1, new int[]{mOutputTextureId}, 0);
        }
        mWidth = 0;
        mHeight = 0;
        mLastThreadID = 0;
        mOutputTextureId = -1;
    }
}
