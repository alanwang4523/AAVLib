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
package com.alanwang.aavlib.image.filters.args;

import com.alanwang.aavlib.image.filters.common.Constants;
import com.alanwang.aavlib.image.filters.common.FilterCategory;
import com.alanwang.aavlib.image.filters.common.FilterConfig;
import com.alanwang.aavlib.image.filters.common.FilterType;
import com.alanwang.aavlib.utils.ALog;
import com.alanwang.aavlib.utils.APP;
import com.alanwang.aavlib.utils.FileUtils;
import com.alanwang.aavlib.utils.GsonUtils;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/5/17 00:01.
 * Mail: alanwang4523@gmail.com
 */
public class StyleFilterArgSetter {
    private FilterArgChangeListener filterArgChangeListener;

    /**
     * set the filter's argument change listener
     * @param filterArgChangeListener
     */
    public void setFilterArgChangeListener(FilterArgChangeListener filterArgChangeListener) {
        this.filterArgChangeListener = filterArgChangeListener;
    }

    /**
     * set filter with alpha
     * @param filterTYpe
     * @param alpha
     */
    public void setFilter(@FilterType int filterTYpe, float alpha) {
        if (filterTYpe == FilterType.TYPE_STYLE_NONE) {
            if (filterArgChangeListener != null) {
                filterArgChangeListener.onFilterEnable(FilterCategory.FC_STYLE, false);
            }
            return;
        }
        String filterName = FilterConfig.getFilterInfo(filterTYpe).name;

        try {
            String basePath = String.format("filters/style/%s/", filterName) + "%s";
            String jsonString = new String(FileUtils.getBytes(
                    APP.INSTANCE.getAssets().open(String.format(basePath, "config.json"))));

            StyleFilterArg styleFilterArg = GsonUtils.getGson().fromJson(jsonString, StyleFilterArg.class);

            styleFilterArg.fshPath = Constants.SUFFIX_ASSETS + styleFilterArg.fshPath;
            for (StyleFilterArg.ImgArg imgArg : styleFilterArg.imgList) {
                imgArg.path = Constants.SUFFIX_ASSETS + String.format(basePath, imgArg.path);
            }

            String finalJsonStr = GsonUtils.getGson().toJson(styleFilterArg);
            ALog.e("arg_json : " + finalJsonStr);
            if (filterArgChangeListener != null) {
                filterArgChangeListener.onFilterEnable(FilterCategory.FC_STYLE, true);
                filterArgChangeListener.onFilterArgChange(FilterCategory.FC_STYLE, StyleFilterArg.TYPE_RESOURCE, finalJsonStr);
                filterArgChangeListener.onFilterArgChange(FilterCategory.FC_STYLE, StyleFilterArg.TYPE_ALPHA, String.valueOf(alpha));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}