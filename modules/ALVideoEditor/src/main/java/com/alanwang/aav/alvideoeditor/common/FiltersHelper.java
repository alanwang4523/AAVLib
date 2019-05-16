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

    /**
     * 获取需要展示的风格滤镜信息
     * @return
     */
    public static List<EffectBean> getStyleFilteList() {
        List<EffectBean> effectList = new ArrayList<>();

        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_NONE,
                getText(R.string.lib_video_editor_video_effect_name_original),
                R.drawable.icon_style_original));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_ROMANTIC,
                getText(R.string.lib_video_editor_video_effect_name_romantic),
                R.drawable.icon_style_romantic));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_FRESH,
                getText(R.string.lib_video_editor_video_effect_name_fresh),
                R.drawable.icon_style_fresh));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_BEAUTIFUL,
                getText(R.string.lib_video_editor_video_effect_name_beautiful),
                R.drawable.icon_style_beautiful));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_PINK,
                getText(R.string.lib_video_editor_video_effect_name_pink),
                R.drawable.icon_style_pink));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_REMINISCENCE,
                getText(R.string.lib_video_editor_video_effect_name_reminiscence),
                R.drawable.icon_style_reminiscence));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_BLUES,
                getText(R.string.lib_video_editor_video_effect_name_blues),
                R.drawable.icon_style_blues));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_COOL,
                getText(R.string.lib_video_editor_video_effect_name_cool),
                R.drawable.icon_style_cool));
        effectList.add(new EffectBean(
                FilterType.TYPE_STYLE_JAPANESE,
                getText(R.string.lib_video_editor_video_effect_name_japanese),
                R.drawable.icon_style_japanese));
        return effectList;
    }

    private static String getText(int textId) {
        return APP.INSTANCE.getContext().getResources().getString(textId);
    }
}