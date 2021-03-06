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

/**
 * Author: AlanWang4523.
 * Date: 19/5/6 00:25.
 * Mail: alanwang4523@gmail.com
 */
public class Constants {
    public static final String SUFFIX_ASSETS = "assets://";
    public static final String SUFFIX_INFILE = "infile://";
    public static final String SUFFIX_EXFILE = "exfile://";

    public static boolean isAssetsPath(String str) {
        return str.startsWith(Constants.SUFFIX_ASSETS);
    }

    public static boolean isInfilePath(String str) {
        return str.startsWith(Constants.SUFFIX_INFILE);
    }

    public static boolean isExfilePath(String str) {
        return str.startsWith(Constants.SUFFIX_EXFILE);
    }
}