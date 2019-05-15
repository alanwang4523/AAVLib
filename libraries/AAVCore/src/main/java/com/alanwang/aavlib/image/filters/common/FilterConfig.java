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

import android.util.SparseArray;

/**
 * Author: AlanWang4523.
 * Date: 19/5/15 23:25.
 * Mail: alanwang4523@gmail.com
 */
public class FilterConfig {
    private static SparseArray<FilterInfo> sFilterList = new SparseArray<>(10);
    
    static {
        sFilterList.put(FilterType.TYPE_STYLE_NONE,         new FilterInfo("style_none"));
        sFilterList.put(FilterType.TYPE_STYLE_ROMANTIC,     new FilterInfo("style_romantic"));
        sFilterList.put(FilterType.TYPE_STYLE_FRESH,        new FilterInfo("style_fresh"));
        sFilterList.put(FilterType.TYPE_STYLE_BEAUTIFUL,    new FilterInfo("style_beautiful"));
        sFilterList.put(FilterType.TYPE_STYLE_PINK,         new FilterInfo("style_pink"));
        sFilterList.put(FilterType.TYPE_STYLE_REMINISCENCE, new FilterInfo("style_reminiscence"));
        sFilterList.put(FilterType.TYPE_STYLE_BLUES,        new FilterInfo("style_blues"));
        sFilterList.put(FilterType.TYPE_STYLE_COOL,         new FilterInfo("style_cool"));
        sFilterList.put(FilterType.TYPE_STYLE_JAPANESE,     new FilterInfo("style_japanese"));
    }
    
    public static FilterInfo getFilterInfo(@FilterType int filterType) {
        return sFilterList.get(filterType);
    }

    public static class FilterInfo {
        public String name;

        public FilterInfo(String name) {
            this.name = name;
        }
    }
}