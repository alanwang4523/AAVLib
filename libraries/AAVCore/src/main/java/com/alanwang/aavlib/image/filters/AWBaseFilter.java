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
package com.alanwang.aavlib.image.filters;

import android.opengl.GLES20;
import com.alanwang.aavlib.image.filters.common.FilterValue;
import com.alanwang.aavlib.opengl.common.AWCoordinateUtil;
import com.alanwang.aavlib.opengl.common.AWFrameBuffer;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import java.nio.FloatBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/4/28 00:34.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWBaseFilter {

    protected String mVertexShader;
    protected String mFragmentShader;
    protected int mProgramHandle = -1;

    // locations
    protected int mAVertexCoordinateLoc;
    protected int mATextureCoordinateLoc;
    protected int mUTextureLoc;

    protected FloatBuffer mVertexCoordinateBuffer;
    protected FloatBuffer mTextureCoordinateBuffer;

    protected AWFrameBuffer mOutputFrameBuffer;
    protected int mTextureWidth;
    protected int mTextureHeight;

    public AWBaseFilter() {
        this(GlUtil.DIRECT_FILTER_VERTEX_SHADER, GlUtil.DIRECT_FILTER_FRAGMENT_SHADER);
    }

    public AWBaseFilter(String vertexSHader, String fragmentShader) {
        mVertexShader = vertexSHader;
        mFragmentShader = fragmentShader;
    }

    public boolean initialize() {
        if (mProgramHandle == -1) {
            mProgramHandle = GlUtil.createProgram(mVertexShader, mFragmentShader);
            if (mProgramHandle <= 0) {
                return false;
            }

            mAVertexCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
            mATextureCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
            mUTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");

            mVertexCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_VERTEX_COORDS);
            mTextureCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
        }
        return true;
    }

    public void updateTextureSize(int textureWidth, int textureHeight) {
        mTextureWidth = textureWidth;
        mTextureHeight = textureHeight;
    }

    public boolean putInputTexture(String name, int texture) {

        return true;
    }

    public boolean putInputValue(String name, FilterValue value) {

        return true;
    }

    public void addTargetFilter(String name, AWBaseFilter outputFilter) {

    }

    public void onDraw() {
        onDraw(null);
    }

    public void onDraw(AWFrameBuffer frameBuffer) {
        onObtainFrameBuffer();
        if (mOutputFrameBuffer != null) {
            mOutputFrameBuffer.bindFrameBuffer();
            mOutputFrameBuffer.checkInit(mTextureWidth, mTextureHeight);

            onGlClear();
            GLES20.glViewport(0, 0, mOutputFrameBuffer.getWidth(), mOutputFrameBuffer.getHeight());
            GLES20.glUseProgram(mProgramHandle);

            onPreDraw();
            onBindCoordinate();
            onInputTextures();
            onInputValues();
            onDrawArrays();
            onPostDraw();

            mOutputFrameBuffer.unbindFrameBuffer();

            onOutputTarget();
        }
    }

    public int getOutputTexture() {
        return GlUtil.INVALID_TEXTURE_ID;
    }

    public void release() {
        try {
            if (this.mProgramHandle != -1) {
                GLES20.glDeleteProgram(this.mProgramHandle);
                this.mProgramHandle = -1;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected boolean needSkip() {
        return true;
    }

    protected void onObtainFrameBuffer() {

    }

    protected void onGlClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    protected void onPreDraw() {

    }

    protected void onBindCoordinate() {
        GLES20.glEnableVertexAttribArray(mAVertexCoordinateLoc);
        GLES20.glVertexAttribPointer(mAVertexCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mVertexCoordinateBuffer);

        GLES20.glEnableVertexAttribArray(mATextureCoordinateLoc);
        GLES20.glVertexAttribPointer(mATextureCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mTextureCoordinateBuffer);

    }

    protected void onInputTextures() {

    }

    protected void onInputValues() {

    }

    protected void onDrawArrays() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    protected void onPostDraw() {

    }

    protected void onOutputTarget() {

    }
}