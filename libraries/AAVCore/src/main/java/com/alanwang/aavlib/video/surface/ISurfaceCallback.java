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
package com.alanwang.aavlib.video.surface;

import android.view.Surface;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 22:57.
 * Mail: alanwang4523@gmail.com
 */

public interface ISurfaceCallback {
    /**
     * onSurfaceChanged
     * @param surface surfaceHolder
     * @param w width
     * @param h height
     */
    void onSurfaceChanged(Surface surface, int w, int h);

    /**
     * surfaceDestroyed
     * @param surface surfaceHolder
     */
    void onSurfaceDestroyed(Surface surface);
}
