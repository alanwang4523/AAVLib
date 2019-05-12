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

import android.util.SparseArray;
import com.alanwang.aavlib.image.filters.common.FilterCategory;
import com.alanwang.aavlib.image.filters.common.ImageTextureCallback;
import com.alanwang.aavlib.image.filters.common.InputStreamCallback;

/**
 * Author: AlanWang4523.
 * Date: 19/5/12 22:47.
 * Mail: alanwang4523@gmail.com
 */
public class AWFilterChain {
    private SparseArray<FilterElement> mFilterList;

    public AWFilterChain(@FilterCategory int[] filterCategoryArr) {
        mFilterList = new SparseArray<>(5);
        for (@FilterCategory int filterCategory : filterCategoryArr) {
            FilterElement filterElement = new FilterElement();
            filterElement.filter = AWFilterFactory.getInstance().createFilter(filterCategory);
            filterElement.filterCategory = filterCategory;
            filterElement.enable = false;
            mFilterList.put(filterCategory, filterElement);
        }
    }

    /**
     * set image texture callback
     * @param imageTextureCallback
     */
    public void setImageTextureCallback(ImageTextureCallback imageTextureCallback) {
        for (int i = 0; i < mFilterList.size(); i++) {
            FilterElement filterElement = mFilterList.valueAt(i);
            filterElement.filter.setImageTextureCallback(imageTextureCallback);
        }
    }

    /**
     * set input stream callback
     * @param inputStreamCallback
     */
    public void setInputStreamCallback(InputStreamCallback inputStreamCallback) {
        for (int i = 0; i < mFilterList.size(); i++) {
            FilterElement filterElement = mFilterList.valueAt(i);
            filterElement.filter.setInputStreamCallback(inputStreamCallback);
        }
    }

    /**
     * enable or disable the filter
     * @param filterCategory
     * @param enable
     */
    public void setFilterEnable(@FilterCategory int filterCategory, boolean enable) {
        FilterElement filterElement = mFilterList.get(filterCategory);
        if (filterElement != null) {
            filterElement.enable = enable;
        }
    }

    /**
     * initialize all filters
     */
    public void initialize() {
        for (int i = 0; i < mFilterList.size(); i++) {
            FilterElement filterElement = mFilterList.valueAt(i);
            filterElement.filter.initialize();
        }
    }

    /**
     * let the input texture pass whole filter chain
     * @param inputTextureId
     * @return
     */
    public int draw(int inputTextureId) {
        boolean isFirstFilter = true;
        int outputTextureId = inputTextureId;
        for (int i = 0; i < mFilterList.size(); i++) {
            AWBaseFilter curFilter = getNextValidFilter(i);
            if (curFilter != null) {
                if (isFirstFilter) {
                    curFilter.putInputTexture(AWBaseFilter.DEFAULT_TEXTURE_NAME, inputTextureId);
                    isFirstFilter = false;
                }

                AWBaseFilter nextFilter = getNextValidFilter(i + 1);
                if (nextFilter != null) {
                    curFilter.clearTargetFilters();
                    curFilter.addTargetFilter(AWBaseFilter.DEFAULT_TEXTURE_NAME, nextFilter);
                } else {
                    curFilter.clearTargetFilters();
                }
                curFilter.onDraw();
                outputTextureId = curFilter.getOutputTextureId();
            }
        }

        return outputTextureId;
    }

    /**
     * get the filters count in filter chain
     * @return
     */
    public int getFilterCount() {
        return mFilterList.size();
    }

    /**
     * get next valid filter
     * @param curIndex
     * @return
     */
    private AWBaseFilter getNextValidFilter(int curIndex) {
        for (int i = curIndex; i < mFilterList.size(); i++) {
            FilterElement filterElement = mFilterList.valueAt(i);
            if (!isNeedSkip(filterElement)) {
                return filterElement.filter;
            }
        }
        return null;
    }


    /**
     * the filter is need skip
     * @param filterElement
     * @return
     */
    private boolean isNeedSkip(FilterElement filterElement) {
        return !filterElement.enable || filterElement.filter.needSkip();
    }

    private static class FilterElement {
        public AWBaseFilter filter;
        public @FilterCategory int filterCategory;
        public boolean enable;
    }
}