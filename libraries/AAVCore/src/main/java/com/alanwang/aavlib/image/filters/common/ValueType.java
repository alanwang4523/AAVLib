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
 * Date: 19/4/28 23:16.
 * Mail: alanwang4523@gmail.com
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ValueType.FLOAT_1, ValueType.FLOAT_2, ValueType.FLOAT_3, ValueType.FLOAT_4,
        ValueType.INT_1})
public @interface ValueType {
    int FLOAT_1 = 101;
    int FLOAT_2 = 102;
    int FLOAT_3 = 103;
    int FLOAT_4 = 104;

    int INT_1   = 201;
}
