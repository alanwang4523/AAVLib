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

import com.alanwang.aavlib.image.filters.args.AlphaArg;
import com.alanwang.aavlib.image.filters.args.FilterArg;
import com.alanwang.aavlib.image.filters.args.StyleFilterArg;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import com.alanwang.aavlib.utils.GsonUtils;
import com.google.gson.JsonSyntaxException;

/**
 * Author: AlanWang4523.
 * Date: 19/5/5 23:10.
 * Mail: alanwang4523@gmail.com
 */
public class AWStyleFilter extends AWBaseFilter {

    private volatile boolean mIsNeedUpdateProgram = false;
    private StyleFilterArg mStyleFilterArg;
    private float alpha = 0.5f;

    public AWStyleFilter() {
        super();
        putInputTexture(DEFAULT_TEXTURE_NAME, GlUtil.GL_INVALID_TEXTURE_ID);
    }

    @Override
    protected void setArgs(int type, String argStr) {
        if (type == FilterArg.TYPE_RESOURCE) {
            try {
                mStyleFilterArg = GsonUtils.getGson().fromJson(argStr, StyleFilterArg.class);
                mIsNeedUpdateProgram = true;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else if (type == FilterArg.TYPE_ALPHA) {
            try {
                alpha = Float.valueOf(argStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDraw(int textureId) {
        putInputTexture(DEFAULT_TEXTURE_NAME, textureId);
        super.onDraw();
    }
}