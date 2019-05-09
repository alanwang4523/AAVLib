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
import com.alanwang.aavlib.image.filters.common.FilterTargetFilter;
import com.alanwang.aavlib.image.filters.common.ImageTextureCallback;
import com.alanwang.aavlib.image.filters.common.InputStreamCallback;
import com.alanwang.aavlib.image.filters.common.ValueType;
import com.alanwang.aavlib.opengl.common.AWCoordinateUtil;
import com.alanwang.aavlib.opengl.common.AWFrameBuffer;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Author: AlanWang4523.
 * Date: 19/4/28 00:34.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWBaseFilter {

    protected static final int MAX_INPUT_TEXTURE_SIZE = 8;
    private static final int GL_TEXTURES[] = {
        GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1, GLES20.GL_TEXTURE2, GLES20.GL_TEXTURE3,
        GLES20.GL_TEXTURE4, GLES20.GL_TEXTURE5, GLES20.GL_TEXTURE5, GLES20.GL_TEXTURE7
    };
    protected static final String DEFAULT_TEXTURE_NAME = "inputImageTexture";

    protected String mVertexShader;
    protected String mFragmentShader;
    protected int mProgramHandle = GlUtil.GL_PROGRAM_INVALID_ID;

    // locations
    protected int mVertexCoordinateLoc;
    protected int mTextureCoordinateLoc;

    protected FloatBuffer mVertexCoordinateBuffer;
    protected FloatBuffer mTextureCoordinateBuffer;
    protected HashMap<String, FilterInputValue> mInputValueMap = new HashMap<>();
    protected HashMap<String, FilterInputTexture> mInputTextureMap = new HashMap<>();
    protected ArrayList<FilterTargetFilter> mTargetFilterList = new ArrayList<>();
    protected AWFrameBuffer mOutputFrameBuffer;
    protected ImageTextureCallback mImageTextureCallback;
    protected InputStreamCallback mInputStreamCallback;
    protected int mTextureWidth;
    protected int mTextureHeight;

    public AWBaseFilter() {
        this(GlUtil.DIRECT_FILTER_VERTEX_SHADER, GlUtil.DIRECT_FILTER_FRAGMENT_SHADER);
    }

    public AWBaseFilter(String vertexShader, String fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    /**
     * 设置纹理回调
     * @param imageTextureCallback
     */
    public void setImageTextureCallback(ImageTextureCallback imageTextureCallback) {
        this.mImageTextureCallback = imageTextureCallback;
    }

    /**
     * 设置获取输入流回调
     * @param inputStreamCallback
     */
    public void setInputStreamCallback(InputStreamCallback inputStreamCallback) {
        this.mInputStreamCallback = inputStreamCallback;
    }

    /**
     * 初始化
     * @return
     */
    public boolean initialize() {
        if (mProgramHandle != GlUtil.GL_PROGRAM_INVALID_ID) {
            GLES20.glDeleteProgram(mProgramHandle);
        }
        mProgramHandle = GlUtil.createProgram(mVertexShader, mFragmentShader);
        if (mProgramHandle <= 0) {
            return false;
        }

        mVertexCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        mTextureCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        mVertexCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_VERTEX_COORDS);
        mTextureCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);

        Collection<FilterInputTexture> filterInputTextureCollection = mInputTextureMap.values();
        for (FilterInputTexture filterInputTexture : filterInputTextureCollection) {
            filterInputTexture.location = GLES20.glGetUniformLocation(mProgramHandle, filterInputTexture.name);
        }

        Collection<FilterInputValue> filterInputValueCollection = mInputValueMap.values();
        for (FilterInputValue filterInputValue : filterInputValueCollection) {
            filterInputValue.location = GLES20.glGetUniformLocation(mProgramHandle, filterInputValue.name);
        }

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
            if (mInputValueMap.size() > MAX_INPUT_TEXTURE_SIZE) {
                return false;
            }
            filterInputTexture = new FilterInputTexture();
            filterInputTexture.name = name;
        }
        filterInputTexture.textureId = textureId;
        mInputTextureMap.put(name, filterInputTexture);
        return true;
    }

    /**
     * 输入一个 float 型参数
     * @param name
     * @param value
     * @return
     */
    public boolean putInputValue(String name, float value) {
        return putInputValue(name, ValueType.FLOAT_1, new float[]{value});
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
        FilterTargetFilter targetFilter = new FilterTargetFilter();
        targetFilter.name = name;
        targetFilter.filter = outputFilter;
        mTargetFilterList.add(targetFilter);
    }

    /**
     * 清除所有目标 filter
     */
    public void clearTargetFilters() {
        mTargetFilterList.clear();
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

            if (sharedFrameBuffer == null) {
                onGlClear();
            }
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
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 获取输出纹理
     * @return
     */
    public int getOutputTextureId() {
        if (mOutputFrameBuffer != null) {
            return mOutputFrameBuffer.getOutputTextureId();
        }
        return GlUtil.GL_INVALID_TEXTURE_ID;
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
            if (mProgramHandle != GlUtil.GL_PROGRAM_INVALID_ID) {
                GLES20.glDeleteProgram(mProgramHandle);
                mProgramHandle = GlUtil.GL_PROGRAM_INVALID_ID;
            }
            if (mOutputFrameBuffer != null) {
                mOutputFrameBuffer.release();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置滤镜参数
     * @param type
     * @param argStr
     */
    public abstract void setArgs(int type, String argStr);

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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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
        GLES20.glEnableVertexAttribArray(mVertexCoordinateLoc);
        GLES20.glVertexAttribPointer(mVertexCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mVertexCoordinateBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateLoc);
        GLES20.glVertexAttribPointer(mTextureCoordinateLoc, 2, GLES20.GL_FLOAT,
                false, 8, mTextureCoordinateBuffer);

    }

    /**
     * 激活并使用输入纹理
     */
    protected void onInputTextures() {
        int i = 0;
        Collection<FilterInputTexture> filterInputTextureCollection = mInputTextureMap.values();
        for (FilterInputTexture filterInputTexture : filterInputTextureCollection) {
            if (filterInputTexture.textureId != GlUtil.GL_INVALID_TEXTURE_ID) {
                GLES20.glActiveTexture(GL_TEXTURES[i]);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterInputTexture.textureId);
                GLES20.glUniform1i(filterInputTexture.location, i);
            }
            i++;
        }
    }

    /**
     * 激活并使用输入参数
     */
    protected void onInputValues() {
        Collection<FilterInputValue> filterInputValueCollection = mInputValueMap.values();
        for (FilterInputValue inputValue : filterInputValueCollection) {
            if (inputValue.valueType == ValueType.FLOAT_1) {
                GLES20.glUniform1f(inputValue.location, inputValue.values[0]);
            } else if (inputValue.valueType == ValueType.FLOAT_2) {
                GLES20.glUniform2f(inputValue.location, inputValue.values[0], inputValue.values[1]);
            } else if (inputValue.valueType == ValueType.FLOAT_3) {
                GLES20.glUniform3f(inputValue.location, inputValue.values[0], inputValue.values[1], inputValue.values[2]);
            } else if (inputValue.valueType == ValueType.FLOAT_4) {
                GLES20.glUniform4f(inputValue.location, inputValue.values[0], inputValue.values[1],
                        inputValue.values[2], inputValue.values[3]);
            } else if (inputValue.valueType == ValueType.INT_1) {
                GLES20.glUniform1i(inputValue.location, (int) inputValue.values[0]);
            }
        }
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
        GLES20.glDisableVertexAttribArray(mVertexCoordinateLoc);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateLoc);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    /**
     * 将结果输出到目标 filter
     */
    protected void onOutputTarget() {
        for (FilterTargetFilter targetFilter : mTargetFilterList) {
            targetFilter.filter.putInputTexture(targetFilter.name, mOutputFrameBuffer.getOutputTextureId());
        }
    }
}