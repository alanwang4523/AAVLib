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
package com.alanwang.aavlib.image.processors;

import com.alanwang.aavlib.image.filters.AWStyleFilter;
import com.alanwang.aavlib.image.filters.args.StyleFilterArg;
import com.alanwang.aavlib.image.filters.common.Constants;
import com.alanwang.aavlib.image.filters.common.FilterCallbackImpl;
import com.alanwang.aavlib.utils.ALog;
import com.alanwang.aavlib.utils.APP;
import com.alanwang.aavlib.utils.FileUtils;
import com.alanwang.aavlib.utils.GsonUtils;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/4/8 23:27.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraPreviewVEProcessor {

    private boolean isInit = false;
    private final AWStyleFilter mTestEffect;
    private FilterCallbackImpl mFilterCallback;
    private int testCount = 0;

    public AWCameraPreviewVEProcessor() {
        mFilterCallback = new FilterCallbackImpl();
        mTestEffect = new AWStyleFilter();
        mTestEffect.setImageTextureCallback(mFilterCallback);
        mTestEffect.setInputStreamCallback(mFilterCallback);
    }

    /**
     * 渲染图像到 surface
     * @param textureId
     * @param textureWidth
     * @param textureHeight
     */
    public int processFrame(int textureId, int textureWidth, int textureHeight) {
        if (!isInit) {
            mTestEffect.initialize();
            isInit = true;
        }
        mTestEffect.updateTextureSize(textureWidth, textureHeight);
        mTestEffect.onDraw(textureId);

        testCount++;
        if (testCount == 100) {
            testStyleFilter();
        }

        return mTestEffect.getOutputTextureId();
    }

    /**
     * 释放资源
     */
    public void release() {
        mTestEffect.release();
    }

    private void testStyleFilter() {
        try {
            mTestEffect.setArgs(StyleFilterArg.TYPE_ALPHA, String.valueOf(0.5f));
            String basePath = "filters/style/style_romantic/%s";
            String jsonString = new String(FileUtils.getBytes(
                    APP.INSTANCE.getAssets().open(String.format(basePath, "config.json"))));

            StyleFilterArg styleFilterArg = GsonUtils.getGson().fromJson(jsonString, StyleFilterArg.class);

            styleFilterArg.fshPath = Constants.SUFFIX_ASSETS + styleFilterArg.fshPath;
            for (StyleFilterArg.ImgArg imgArg : styleFilterArg.imgList) {
                imgArg.path = Constants.SUFFIX_ASSETS + String.format(basePath, imgArg.path);
            }

            ALog.e("src_json : " + jsonString);
            String finalJsonStr = GsonUtils.getGson().toJson(styleFilterArg);
            ALog.e("arg_json : " + finalJsonStr);
            mTestEffect.setArgs(StyleFilterArg.TYPE_RESOURCE, finalJsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
