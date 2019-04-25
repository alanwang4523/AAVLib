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
package com.alanwang.aavlib.video.opengl.engine;

import android.view.Surface;
import com.alanwang.aavlib.video.opengl.common.AWMessage;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 01:20.
 * Mail: alanwang4523@gmail.com
 */

public interface IGLEngineCallback {
    /**
     * Engine start
     */
    void onEngineStart();

    /**
     * Surface update
     * @param surface
     * @param width
     * @param height
     */
    void onSurfaceUpdate(Surface surface, int width, int height);

    /**
     * to render
     * @param msg
     */
    void onRender(AWMessage msg);

    /**
     * 处理非渲染消息
     * @param msg
     */
    void onHandleMsg(AWMessage msg);

    /**
     * Surface destroy
     */
    void onSurfaceDestroy();

    /**
     * Engine release
     */
    void onEngineRelease();
}
