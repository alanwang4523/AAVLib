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
package com.alanwang.aavlib.video.common;

/**
 * 编码时间提供器，可以由 SDK 外部提供编码时间戳，
 * 如用音频的时间戳作为视频的编码时间戳以便于做音画同步
 *
 * Author: AlanWang4523.
 * Date: 19/1/3 15:41.
 * Mail: alanwang4523@gmail.com
 */

public interface IEncodeTimeProvider {
    /**
     * 获取编码时间戳，单位 ms
     * @return
     */
    long getTimeStampMS();
}