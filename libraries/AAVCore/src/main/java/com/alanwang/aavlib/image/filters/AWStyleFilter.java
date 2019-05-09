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

import com.alanwang.aavlib.image.filters.args.FilterArg;
import com.alanwang.aavlib.image.filters.args.StyleFilterArg;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import com.alanwang.aavlib.utils.FileUtils;
import com.alanwang.aavlib.utils.GsonUtils;
import com.google.gson.JsonSyntaxException;

/**
 * Author: AlanWang4523.
 * Date: 19/5/5 23:10.
 * Mail: alanwang4523@gmail.com
 */
public class AWStyleFilter extends AWBaseFilter {
    private static final String DEFAULT_STYLE_FRAGMENT_SHADER = "\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float alpha;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    private static final String NAME_ALPHA = "alpha";
    private volatile boolean mIsNeedUpdateProgram = false;
    private StyleFilterArg mStyleFilterArg;
    private int mImgNum = 0;
    private float mAlpha = 0.5f;

    public AWStyleFilter() {
        super();
        putInputTexture(DEFAULT_TEXTURE_NAME, GlUtil.GL_INVALID_TEXTURE_ID);
        putInputValue(NAME_ALPHA, mAlpha);
    }

    @Override
    protected boolean needSkip() {
        if (mAlpha <= 0.01f) {
            return true;
        }
        return super.needSkip();
    }

    @Override
    public void setArgs(int type, String argStr) {
        if (type == FilterArg.TYPE_RESOURCE) {
            try {
                mStyleFilterArg = GsonUtils.getGson().fromJson(argStr, StyleFilterArg.class);
                if (mStyleFilterArg.imgList != null) {
                    mImgNum = mStyleFilterArg.imgList.size();
                }
                mIsNeedUpdateProgram = true;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else if (type == FilterArg.TYPE_ALPHA) {
            try {
                mAlpha = Float.valueOf(argStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDraw(int textureId) {
        putInputValue(NAME_ALPHA, mAlpha);
        putInputTexture(DEFAULT_TEXTURE_NAME, textureId);
        if (mStyleFilterArg != null) {
            if (mImgNum > 0) {
                for (StyleFilterArg.ImgArg imgArg : mStyleFilterArg.imgList) {
                    putInputTexture(imgArg.name, mImageTextureCallback.getTextureId(imgArg.path));
                }
            }
            if (mIsNeedUpdateProgram) {
                mFragmentShader = new String(FileUtils.getBytes(
                        mInputStreamCallback.getInputStream(
                        mStyleFilterArg.fshPath)));
                initialize();
                mIsNeedUpdateProgram = false;
            }
        }
        super.onDraw();
    }
}