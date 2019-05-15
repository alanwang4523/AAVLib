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

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: AlanWang4523.
 * Date: 19/5/15 23:11.
 * Mail: alanwang4523@gmail.com
 */

@IntDef({
     /******* Type Style Begin *******/
     FilterType.TYPE_STYLE_NONE,
     FilterType.TYPE_STYLE_ROMANTIC,
     FilterType.TYPE_STYLE_FRESH,
     FilterType.TYPE_STYLE_BEAUTIFUL,
     FilterType.TYPE_STYLE_PINK,
     FilterType.TYPE_STYLE_REMINISCENCE,
     FilterType.TYPE_STYLE_BLUES,
     FilterType.TYPE_STYLE_COOL,
     FilterType.TYPE_STYLE_JAPANESE,
     /*******  Type Style End  *******/
})
@Retention(RetentionPolicy.SOURCE)
public @interface FilterType {
    int TYPE_STYLE_NONE          = 1001; // 原图
    int TYPE_STYLE_ROMANTIC      = 1002; // 浪漫
    int TYPE_STYLE_FRESH         = 1003; // 清新
    int TYPE_STYLE_BEAUTIFUL     = 1004; // 唯美
    int TYPE_STYLE_PINK          = 1005; // 粉嫩
    int TYPE_STYLE_REMINISCENCE  = 1006; // 怀旧
    int TYPE_STYLE_BLUES         = 1007; // 蓝调
    int TYPE_STYLE_COOL          = 1008; // 清凉
    int TYPE_STYLE_JAPANESE      = 1009; // 日系
}
