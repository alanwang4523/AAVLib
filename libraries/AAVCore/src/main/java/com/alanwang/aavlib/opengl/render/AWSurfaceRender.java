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
package com.alanwang.aavlib.opengl.render;

import android.opengl.GLES20;
import com.alanwang.aavlib.opengl.common.AWCoordinateUtil;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import java.nio.FloatBuffer;

/**
 * AWSurfaceRender
 * 通用的 AWSurfaceRender ，支持将纹理渲染到 FrameBuffer、屏幕、编码器
 *
 * Author: AlanWang4523.
 * Date: 19/1/23 00:29.
 * Mail: alanwang4523@gmail.com
 */

public class AWSurfaceRender extends AWBaseRender {

    private FloatBuffer mTextureCoordinateFBuffer;
    private int viewWidth;
    private int viewHeight;

    public AWSurfaceRender() {
        super();
        mTextureCoordinateFBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
    }

    /**
     * 更新显示窗口大小
     * @param viewWidth
     * @param viewHeight
     */
    public void updateViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * 更新纹理坐标
     * @param fArr
     */
    public void updateTextureCoordinates(float[] fArr) {
        this.mTextureCoordinateFBuffer.put(fArr).position(0);
    }

    /**
     * 如果调用了 {@link #updateViewSize} 则可以调用该方法绘制
     * @param textureId
     */
    public void drawFrame(int textureId) {
        drawFrame(textureId, viewWidth, viewHeight);
    }

    /**
     * 渲染数据到 surface
     * @param textureId 要渲染的纹理
     * @param width 视口的宽
     * @param height 视口的高
     */
    public void drawFrame(int textureId, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        super.drawFrame(textureId, DEFAULT_VERTEX_COORDINATE_BUFFER, mTextureCoordinateFBuffer);
    }
}
