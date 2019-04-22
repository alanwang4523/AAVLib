package com.alanwang.aavlib.libvideoeffect.processors;

import com.alanwang.aavlib.libeglcore.common.AWFrameBuffer;
import com.alanwang.aavlib.libvideoeffect.effects.AWGrayEffect;

/**
 * Author: AlanWang4523.
 * Date: 19/4/8 23:27.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraPreviewVEProcessor {

    private final AWFrameBuffer mEffectFrameBuffer;
    private final AWGrayEffect mTestEffect;

    public AWCameraPreviewVEProcessor() {
        mEffectFrameBuffer = new AWFrameBuffer();
        mTestEffect = new AWGrayEffect();
    }

    /**
     * 渲染图像到 surface
     * @param textureId
     * @param textureWidth
     * @param textureHeight
     */
    public int processFrame(int textureId, int textureWidth, int textureHeight) {
        mEffectFrameBuffer.checkInit(textureWidth, textureHeight);
        mEffectFrameBuffer.bindFrameBuffer();
        mTestEffect.drawFrame(textureId);
        mEffectFrameBuffer.unbindFrameBuffer();

        return mEffectFrameBuffer.getOutputTextureId();
    }

    /**
     * 释放资源
     */
    public void release() {
        mEffectFrameBuffer.release();
        mTestEffect.release();
    }
}
