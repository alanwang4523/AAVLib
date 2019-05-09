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
package com.alanwang.aavlib.image.filters.common;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import com.alanwang.aavlib.opengl.egl.GlUtil;
import com.alanwang.aavlib.utils.ALog;
import com.alanwang.aavlib.utils.APP;
import com.alanwang.aavlib.utils.BitmapUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

/**
 * Author: AlanWang4523.
 * Date: 19/5/9 23:38.
 * Mail: alanwang4523@gmail.com
 */
public class FilterCallbackImpl implements ImageTextureCallback, InputStreamCallback {

    private HashMap<String, Integer> textureMap = new HashMap<>();

    @Override
    public int getTextureId(String imgPath) {

        int textureId = 0;
        if (!textureMap.containsKey(imgPath)) {
            Bitmap bitmap = BitmapUtils.getBitmapByPath(APP.INSTANCE.getContext(), imgPath);
            if (bitmap != null) {
                textureId = GlUtil.loadImageTexture(bitmap);
                if (textureId >= 0) {
                    textureMap.put(imgPath, textureId);
                }
            }
        } else {
            textureId = textureMap.get(imgPath);
        }
        ALog.e("getImageTexture()--->>textureId = " + textureId + ", imgPath = " + imgPath);
        return textureId;
    }

    @Override
    public InputStream getInputStream(String filePath) {
        ALog.e("getInputStream()--->>filePath = " + filePath);
        if (Constants.isAssetsPath(filePath)) {
            String realPath = filePath.replace(Constants.SUFFIX_ASSETS, "");
            try {
                return APP.INSTANCE.getContext().getAssets().open(realPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Constants.isExfilePath(filePath)) {
            String realPath = filePath.replace(Constants.SUFFIX_EXFILE, "");
            try {
                return new FileInputStream(realPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 释放资源
     */
    public void release() {
        Collection<Integer> textureList = textureMap.values();
        for (Integer texture: textureList) {
            ALog.d("releaseAll()--->>texture = " + texture);
            if (texture > 0) {
                GLES20.glDeleteTextures(1, new int[]{texture}, 0);
            }
        }
        textureMap.clear();
    }
}