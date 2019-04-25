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
package com.alanwang.aavlib.video.effect.processors;

import com.alanwang.aavlib.video.opengl.common.AWFrameBuffer;
import com.alanwang.aavlib.video.effect.effects.AWGrayEffect;

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
