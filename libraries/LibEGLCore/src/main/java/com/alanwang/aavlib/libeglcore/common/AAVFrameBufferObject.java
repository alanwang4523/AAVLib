package com.alanwang.aavlib.libeglcore.common;

import android.opengl.GLES20;

import com.alanwang.aavlib.libeglcore.egl.GlUtil;

/**
 * AAVFrameBufferObject 自带 texture 的 AAVFrameBuffer
 * Author: AlanWang4523.
 * Date: 19/1/22 00:58.
 * Mail: alanwang4523@gmail.com
 */

public class AAVFrameBufferObject {

    private AAVFrameBuffer mFrameBuffer;
    private long mLastThreadID = 0;
    private int mOutputTextureId = -1;
    private int mWidth = 0;
    private int mHeight = 0;

    public AAVFrameBufferObject() {
        mFrameBuffer = new AAVFrameBuffer();
    }

    public boolean checkInit(int width, int height) {
        long id = Thread.currentThread().getId();
        if (mLastThreadID != id && mLastThreadID > 0) {
            releaseFrameBuffer();
        } else if (mWidth == width && mHeight == height && mOutputTextureId != -1) {
            return true;
        } else {
            releaseFrameBuffer();
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
    public void releaseFrameBuffer() {
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
