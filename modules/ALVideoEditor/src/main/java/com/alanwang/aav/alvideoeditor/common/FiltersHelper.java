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
package com.alanwang.aav.alvideoeditor.common;

import android.util.SparseArray;

import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.EffectBean;
import com.alanwang.aavlib.image.filters.common.FilterType;
import com.alanwang.aavlib.utils.APP;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/5/16 23:28.
 * Mail: alanwang4523@gmail.com
 */
public class FiltersHelper {
    private static SparseArray<EffectBean> sStyleFiltersMap;

    /**
     * 根据类型获取滤镜信息
     * @param filterType
     * @return
     */
    public static EffectBean getStyleFilterBean(@FilterType int filterType) {
        checkInitStyleFilters();
        return sStyleFiltersMap.get(filterType);
    }

    /**
     * 获取需要展示的风格滤镜信息
     * @return
     */
    public static List<EffectBean> getStyleFilterList() {
        checkInitStyleFilters();

        List<EffectBean> effectList = new ArrayList<>();
        for (int i = 0; i < sStyleFiltersMap.size(); i++) {
            effectList.add(sStyleFiltersMap.valueAt(i));
        }

        return effectList;
    }

    private static void checkInitStyleFilters() {
        if (sStyleFiltersMap == null) {
            sStyleFiltersMap = new SparseArray<>(10);

            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_NONE,
                    new EffectBean(
                            FilterType.TYPE_STYLE_NONE,
                            getText(R.string.lib_video_editor_video_effect_name_original),
                            R.drawable.icon_style_original));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_ROMANTIC,
                    new EffectBean(
                            FilterType.TYPE_STYLE_ROMANTIC,
                            getText(R.string.lib_video_editor_video_effect_name_romantic),
                            R.drawable.icon_style_romantic));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_FRESH,
                    new EffectBean(
                            FilterType.TYPE_STYLE_FRESH,
                            getText(R.string.lib_video_editor_video_effect_name_fresh),
                            R.drawable.icon_style_fresh));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_BEAUTIFUL,
                    new EffectBean(
                            FilterType.TYPE_STYLE_BEAUTIFUL,
                            getText(R.string.lib_video_editor_video_effect_name_beautiful),
                            R.drawable.icon_style_beautiful));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_PINK,
                    new EffectBean(
                            FilterType.TYPE_STYLE_PINK,
                            getText(R.string.lib_video_editor_video_effect_name_pink),
                            R.drawable.icon_style_pink));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_REMINISCENCE,
                    new EffectBean(
                            FilterType.TYPE_STYLE_REMINISCENCE,
                            getText(R.string.lib_video_editor_video_effect_name_reminiscence),
                            R.drawable.icon_style_reminiscence));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_BLUES,
                    new EffectBean(
                            FilterType.TYPE_STYLE_BLUES,
                            getText(R.string.lib_video_editor_video_effect_name_blues),
                            R.drawable.icon_style_blues));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_COOL,
                    new EffectBean(
                            FilterType.TYPE_STYLE_COOL,
                            getText(R.string.lib_video_editor_video_effect_name_cool),
                            R.drawable.icon_style_cool));
            sStyleFiltersMap.put(
                    FilterType.TYPE_STYLE_JAPANESE,
                    new EffectBean(
                            FilterType.TYPE_STYLE_JAPANESE,
                            getText(R.string.lib_video_editor_video_effect_name_japanese),
                            R.drawable.icon_style_japanese));
        }
    }

    private static String getText(int textId) {
        return APP.INSTANCE.getContext().getResources().getString(textId);
    }
}