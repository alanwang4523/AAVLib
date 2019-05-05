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

import com.alanwang.aavlib.opengl.egl.GlUtil;

/**
 * Author: AlanWang4523.
 * Date: 19/5/5 23:10.
 * Mail: alanwang4523@gmail.com
 */
public class AWStyleFilter extends AWBaseFilter {

    private static final String TEST_FRAGMENT_SHADER =
        "precision highp float;\n" +
        "varying vec2 textureCoordinate;\n" +
        "uniform sampler2D inputImageTexture;\n" +
        "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
        "void main()\n" +
        "{\n" +
        "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
        "    float luminance = dot(textureColor.rgb, W);\n" +
        "    gl_FragColor = vec4(vec3(luminance), textureColor.a);\n" +
        "}";

    public AWStyleFilter() {
        super(GlUtil.DIRECT_FILTER_VERTEX_SHADER, TEST_FRAGMENT_SHADER);
        putInputTexture(DEFAULT_TEXTURE_NAME, GlUtil.GL_INVALID_TEXTURE_ID);
    }

    public void onDraw(int textureId) {
        putInputTexture(DEFAULT_TEXTURE_NAME, textureId);
        super.onDraw();
    }
}