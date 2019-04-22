package com.alanwang.aavlib.libvideo.core;

import com.alanwang.aavlib.libeglcore.common.AWFrameBuffer;
import com.alanwang.aavlib.libeglcore.render.AWSurfaceRender;
import com.alanwang.aavlib.libvideoeffect.effects.AWGrayEffect;

/**
 * Author: AlanWang4523.
 * Date: 19/2/17 18:35.
 * Mail: alanwang4523@gmail.com
 */

public class AWVideoPreviewRender {
    private final AWFrameBuffer mEffectFrameBuffer;
    private final AWGrayEffect mTestEffect;
    private final AWSurfaceRender mVideoRender;

    private int mViewportWidth;
    private int mViewportHeight;

    public AWVideoPreviewRender() {
        mEffectFrameBuffer = new AWFrameBuffer();
        mTestEffect = new AWGrayEffect();
        mVideoRender = new AWSurfaceRender();
    }

    /**
     * 更新预览窗口的大小
     * @param viewWidth
     * @param viewHeight
     */
    public void updatePreviewSize(int viewWidth, int viewHeight) {
        mViewportWidth = viewWidth;
        mViewportHeight = viewHeight;
    }

    /**
     * 渲染图像到 surface
     * @param textureId
     * @param textureWidth
     * @param textureHeight
     */
    public void draw(int textureId, int textureWidth, int textureHeight) {

        mEffectFrameBuffer.checkInit(textureWidth, textureHeight);
        mEffectFrameBuffer.bindFrameBuffer();
        mTestEffect.drawFrame(textureId);
        mEffectFrameBuffer.unbindFrameBuffer();

        mVideoRender.drawFrame(mEffectFrameBuffer.getOutputTextureId(), mViewportWidth, mViewportHeight);
    }

    /**
     * 释放资源
     */
    public void release() {
        mEffectFrameBuffer.release();
        mTestEffect.release();
        mVideoRender.release();
    }

}
