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

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.alanwang.aavlib.opengl.common.AWSurfaceTexture;

/**
 * AWOESTextureRender
 * 用于 OES 纹理渲染，OES 纹理可来源于相机、player、编码器
 *
 * Author: AlanWang4523.
 * Date: 19/1/23 00:19.
 * Mail: alanwang4523@gmail.com
 */

public class AWOESTextureRender extends AWBaseRender {

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mTextureTransformMatrix = new float[16];
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;

    public AWOESTextureRender() {
        super(VERTEX_SHADER, FRAGMENT_SHADER, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        Matrix.setIdentityM(this.mTextureTransformMatrix, 0);
    }

    /**
     * 沿 Z 轴旋转
     * @param i
     */
    public void rotate(int i) {
        resetMVPMatrix();
        rotate(i, 0, 0, 1);
    }

    /**
     * 沿各轴旋转
     * @param angle
     * @param x
     * @param y
     * @param z
     */
    public void rotate(int angle, int x, int y, int z) {
        Matrix.rotateM(this.mMVPMatrix, 0, (float) angle, (float) x, (float) y, (float) z);
    }

    /**
     * 重置 MVP 矩阵
     */
    public void resetMVPMatrix() {
        Matrix.setIdentityM(this.mMVPMatrix, 0);
    }

    /**
     * 渲染纹理
     * @param AWSurfaceTexture
     */
    public void drawFrame(AWSurfaceTexture AWSurfaceTexture) {
        AWSurfaceTexture.updateTexImage(mTextureTransformMatrix);
        super.drawFrame(AWSurfaceTexture.getTextureId(), DEFAULT_VERTEX_COORDINATE_BUFFER, DEFAULT_TEXTURE_COORDINATE_BUFFER);
    }

    /**
     * 渲染纹理
     * @param textureId
     */
    public void drawFrame(int textureId) {
        super.drawFrame(textureId, DEFAULT_VERTEX_COORDINATE_BUFFER, DEFAULT_TEXTURE_COORDINATE_BUFFER);
    }

    @Override
    protected boolean onInitialized() {
        this.muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        this.muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uSTMatrix");
        return true;
    }

    @Override
    protected void preDraw() {
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mTextureTransformMatrix, 0);
    }
}
