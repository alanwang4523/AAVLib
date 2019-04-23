/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
