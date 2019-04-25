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

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 00:50.
 * Mail: alanwang4523@gmail.com
 */

public class AWOnlyFrameBuffer {
    private int mFBO = -1;
    private long mLastThreadID = 0;

    public AWOnlyFrameBuffer() {

    }

    /**
     * 创建一个 FrameBuffer
     */
    private void createFrameBuffer() {
        // 创建 FBO
        int[] fboArr = new int[1];
        GLES20.glGenFramebuffers(1, fboArr, 0);
        mFBO = fboArr[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * 检测是否初始化
     */
    public void checkInit() {
        long id = Thread.currentThread().getId();
        if (mLastThreadID != id && mLastThreadID > 0) {
            release();
        } else if (mFBO != -1) {
            return;
        }
        mLastThreadID = id;
        createFrameBuffer();
    }

    /**
     * 将 texture 绑定到 FrameBuffer
     * @param textureId
     */
    public void bindFBOWithTexture(int textureId) {
        bindFrameBuffer();
        bindTexture2FBO(textureId);
    }

    /**
     * 将 texture 从 FrameBuffer 解绑
     */
    public void unBindFBOWithTexture() {
        unBindTexture2FBO();
        unbindFrameBuffer();
    }

    /**
     * 绑定 texture 到 FrameBuffer，调该方法的前置条件是已经 bindFrameBuffer()
     * 将其与 bindFrameBuffer() 拆分使调用更灵活
     * @param textureId
     */
    public void bindTexture2FBO(int textureId) {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
    }

    /**
     * 解绑 texture
     */
    public void unBindTexture2FBO() {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
    }

    /**
     * 绑定 FrameBuffer
     */
    public void bindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBO);
    }

    /**
     * 解绑 FrameBuffer
     */
    public void unbindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mFBO != -1) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBO);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDeleteFramebuffers(1, new int[]{mFBO}, 0);
        }
        this.mFBO = -1;
        mLastThreadID = 0;
    }
}
