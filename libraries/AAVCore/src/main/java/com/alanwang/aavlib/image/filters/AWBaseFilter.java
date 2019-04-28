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

import com.alanwang.aavlib.opengl.common.AWCoordinateUtil;
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

    public AWBaseFilter() {
        this(GlUtil.DIRECT_FILTER_VERTEX_SHADER, GlUtil.DIRECT_FILTER_FRAGMENT_SHADER);
    }

    public AWBaseFilter(String vertexSHader, String fragmentShader) {
        mVertexShader = vertexSHader;
        mFragmentShader = fragmentShader;
    }

    public void initialize() {
        mVertexCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_VERTEX_COORDS);
        mTextureCoordinateBuffer = GlUtil.createFloatBuffer(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
    }
}