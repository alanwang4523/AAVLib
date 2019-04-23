/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libmediacore.listener;

import com.alanwang.aavlib.libmediacore.exception.AWException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/15 13:18.
 * Mail: alanwang4523@gmail.com
 */
public interface AWResultListener<T> {
    /**
     * 成功的回调
     * @param result
     */
    void onSuccess(T result);

    /**
     * 错误回调，可通过 e 获取错误码和错误信息
     */
    void onError(AWException e);
}
