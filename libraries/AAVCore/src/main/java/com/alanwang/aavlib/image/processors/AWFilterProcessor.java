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

import android.util.SparseArray;
import com.alanwang.aavlib.image.filters.AWFilterChain;
import com.alanwang.aavlib.image.filters.args.FilterArgChangeListener;
import com.alanwang.aavlib.image.filters.args.StyleFilterArgSetter;
import com.alanwang.aavlib.image.filters.common.FilterCategory;
import com.alanwang.aavlib.image.filters.common.FilterType;

/**
 * Author: AlanWang4523.
 * Date: 19/5/17 00:13.
 * Mail: alanwang4523@gmail.com
 */
public class AWFilterProcessor {
    private AWFilterChain mFilterChain;
    private StyleFilterArgSetter styleFilterArgSetter;
    protected SparseArray<Float> mFilterLevelSetBeforeInit;
    private volatile boolean mIsInit = false;

    public AWFilterProcessor(@FilterCategory int[] filterCategoryArr) {
        mFilterChain = new AWFilterChain(filterCategoryArr);
        styleFilterArgSetter = new StyleFilterArgSetter();
        styleFilterArgSetter.setFilterArgChangeListener(mFilterArgChangeListener);
        mFilterLevelSetBeforeInit = new SparseArray<>(10);
    }

    /**
     * set filter with the default alpha 0.5f
     * @param filterTYpe
     */
    public void setFilter(@FilterType int filterTYpe) {
        setFilter(filterTYpe, 0.5f);
    }

    /**
     * set filter with the default alpha
     * @param filterTYpe
     * @param alpha
     */
    public void setFilter(@FilterType int filterTYpe, float alpha) {
        if (!mIsInit) {
            mFilterLevelSetBeforeInit.put(filterTYpe, alpha);
        }
        styleFilterArgSetter.setFilter(filterTYpe, alpha);
    }

    /**
     * process the texture
     * @param textureId
     * @param textureWidth
     * @param textureHeight
     * @return
     */
    public int processFrame(int textureId, int textureWidth, int textureHeight) {
        if (!mIsInit) {
            mFilterChain.initialize();
            handleFilterArgSetBeforeInit();
            mIsInit = true;
        }
        return mFilterChain.draw(textureId, textureWidth, textureHeight);
    }

    /**
     * release the filter chain
     */
    public void release() {
        mFilterChain.release();
    }

    private void handleFilterArgSetBeforeInit() {
        if (mFilterLevelSetBeforeInit.size() > 0) {
            for (int i = 0; i < mFilterLevelSetBeforeInit.size(); i++) {
                @FilterType int filterType = mFilterLevelSetBeforeInit.keyAt(i);
                float level = mFilterLevelSetBeforeInit.get(filterType);
                styleFilterArgSetter.setFilter(filterType, level);
            }
            mFilterLevelSetBeforeInit.clear();
        }
    }

    private FilterArgChangeListener mFilterArgChangeListener = new FilterArgChangeListener() {
        @Override
        public void onFilterEnable(int filterCategory, boolean enable) {
            mFilterChain.setFilterEnable(filterCategory, enable);
        }

        @Override
        public void onFilterArgChange(int filterCategory, int argType, String jsonStr) {
            mFilterChain.setFilterArg(filterCategory, argType, jsonStr);
        }
    };
}