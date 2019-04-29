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
import android.text.TextUtils;
import com.alanwang.aavlib.image.filters.common.FilterInputTexture;
import com.alanwang.aavlib.image.filters.common.FilterInputValue;
import com.alanwang.aavlib.image.filters.common.ValueType;
import com.alanwang.aavlib.opengl.common.AWCoordinateUtil;
import com.alanwang.aavlib.opengl.common.AWFrameBuffer;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import java.nio.FloatBuffer;
import java.util.HashMap;

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
    protected HashMap<String, FilterInputValue> mInputValueMap = new HashMap<>();
    protected HashMap<String, FilterInputTexture> mInputTextureMap = new HashMap<>();
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

    /**
     * 初始化
     * @return
     */
    public boolean initialize() {
        mProgramHandle = GlUtil.createProgram(mVertexShader, mFragmentShader);
        if (mProgramHandle <= 0) {
            return false;
        }

        mAVertexCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        mATextureCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        mUTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");

        mVertexCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_VERTEX_COORDS);
        mTextureCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
        return true;
    }

    /**
     * 更新纹理大小
     * @param textureWidth
     * @param textureHeight
     */
    public void updateTextureSize(int textureWidth, int textureHeight) {
        mTextureWidth = textureWidth;
        mTextureHeight = textureHeight;
    }

    /**
     * 传入输入纹理
     * @param name
     * @param textureId
     * @return
     */
    public boolean putInputTexture(String name, int textureId) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        FilterInputTexture filterInputTexture = mInputTextureMap.get(name);
        if (filterInputTexture == null) {
            filterInputTexture = new FilterInputTexture();
        }
        filterInputTexture.name = name;
        filterInputTexture.textureId = textureId;
        return true;
    }

    /**
     * 传入输出参数
     * @param name
     * @param valueType
     * @param values
     * @return
     */
    public boolean putInputValue(String name, @ValueType int valueType, float[] values) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        FilterInputValue filterInputValue = mInputValueMap.get(name);
        if (filterInputValue == null) {
            filterInputValue = new FilterInputValue();
        }
        filterInputValue.name = name;
        filterInputValue.valueType = valueType;
        filterInputValue.values = values.clone();
        mInputValueMap.put(name, filterInputValue);
        return true;
    }

    /**
     * 添加当前 filter 输出的目标 filter，当前的输出会作为下个目标 filter 的输入
     * @param name
     * @param outputFilter
     */
    public void addTargetFilter(String name, AWBaseFilter outputFilter) {

    }

    /**
     * 绘制
     */
    public void onDraw() {
        onDraw(null);
    }

    /**
     * 绘制，可以由外部传入共享的 frame buffer
     * @param sharedFrameBuffer
     */
    public void onDraw(AWFrameBuffer sharedFrameBuffer) {
        AWFrameBuffer frameBuffer = onObtainFrameBuffer(sharedFrameBuffer);
        if (frameBuffer != null) {
            frameBuffer.bindFrameBuffer();

            onGlClear();
            GLES20.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
            GLES20.glUseProgram(mProgramHandle);

            onPreDraw();
            onBindCoordinate();
            onInputTextures();
            onInputValues();
            onDrawArrays();
            onPostDraw();

            frameBuffer.unbindFrameBuffer();

            onOutputTarget();
        }
    }

    /**
     * 获取输出纹理
     * @return
     */
    public int getOutputTexture() {
        if (mOutputFrameBuffer != null) {
            return mOutputFrameBuffer.getOutputTextureId();
        }
        return GlUtil.INVALID_TEXTURE_ID;
    }

    /**
     * 获取输出的 frame buffer
     * @return
     */
    public AWFrameBuffer getOutputFrameBuffer() {
        return mOutputFrameBuffer;
    }

    /**
     * 释放资源
     */
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

    /**
     * 是否需要跳过该 filter
     * @return
     */
    protected boolean needSkip() {
        return false;
    }

    /**
     * 获取 frame buffer
     * @param sharedFrameBuffer
     * @return
     */
    protected AWFrameBuffer onObtainFrameBuffer(AWFrameBuffer sharedFrameBuffer) {
        if (sharedFrameBuffer == null) {
            if (mOutputFrameBuffer == null) {
                mOutputFrameBuffer = new AWFrameBuffer();
            }
        } else {
            mOutputFrameBuffer = sharedFrameBuffer;
        }
        mOutputFrameBuffer.checkInit(mTextureWidth, mTextureHeight);
        return mOutputFrameBuffer;
    }

    /**
     * 清除 GL 缓冲区
     */
    protected void onGlClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * 预绘制
     */
    protected void onPreDraw() {

    }

    /**
     * 绑定顶点左边和纹理坐标
     */
    protected void onBindCoordinate() {
        GLES20.glEnableVertexAttribArray(mAVertexCoordinateLoc);
        GLES20.glVertexAttribPointer(mAVertexCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mVertexCoordinateBuffer);

        GLES20.glEnableVertexAttribArray(mATextureCoordinateLoc);
        GLES20.glVertexAttribPointer(mATextureCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mTextureCoordinateBuffer);

    }

    /**
     * 激活并使用输入纹理
     */
    protected void onInputTextures() {

    }

    /**
     * 激活并使用输入参数
     */
    protected void onInputValues() {

    }

    /**
     * 绘制
     */
    protected void onDrawArrays() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    /**
     * 后绘制
     */
    protected void onPostDraw() {

    }

    /**
     * 将结果输出到目标 filter
     */
    protected void onOutputTarget() {

    }
}