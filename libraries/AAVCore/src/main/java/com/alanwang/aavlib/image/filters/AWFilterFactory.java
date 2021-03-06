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

import com.alanwang.aavlib.image.filters.common.FilterCategory;

/**
 * Author: AlanWang4523.
 * Date: 19/5/11 23:03.
 * Mail: alanwang4523@gmail.com
 */
public class AWFilterFactory {

    private static class SingletonFactory {
        private static AWFilterFactory sInstance = new AWFilterFactory();
    }

    public static AWFilterFactory getInstance() {
        return SingletonFactory.sInstance;
    }

    /**
     * create a filter by category
     * @param filterCategory
     * @return
     */
    public AWBaseFilter createFilter(@FilterCategory int filterCategory) {
        AWBaseFilter filter = null;
        switch (filterCategory) {
            case FilterCategory.FC_STYLE:
                filter = new AWStyleFilter();
                break;
        }
        return filter;
    }

    private AWFilterFactory() {}
}