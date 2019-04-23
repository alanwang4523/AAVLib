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
package com.alanwang.aavlib.libvideoeffect.effects;

import android.opengl.GLES20;
import com.alanwang.aavlib.libeglcore.render.AWBaseRender;

/**
 * Author: AlanWang4523.
 * Date: 19/1/28 23:44.
 * Mail: alanwang4523@gmail.com
 */

public class AWGrayEffect extends AWBaseRender {

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main() {\n" +
            "    vec4 tc = texture2D(uTexture, vTextureCoord);\n" +
            "    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;\n" +
            "    gl_FragColor = vec4(color, color, color, 1.0);\n" +
            "}\n";

    public AWGrayEffect() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER, GLES20.GL_TEXTURE_2D);
    }

    public void drawFrame(int textureId) {
        super.drawFrame(textureId, DEFAULT_VERTEX_COORDINATE_BUFFER, DEFAULT_TEXTURE_COORDINATE_BUFFER);
    }
}
