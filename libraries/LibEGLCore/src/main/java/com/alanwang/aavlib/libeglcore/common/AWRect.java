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
package com.alanwang.aavlib.libeglcore.common;

import android.graphics.Rect;

/**
 * Author: AlanWang4523.
 * Date: 19/1/23 00:42.
 * Mail: alanwang4523@gmail.com
 */

public class AWRect {
    /**
     * 左上角的 x 坐标
     */
    public int x;

    /**
     * 左上角的 y 坐标
     */
    public int y;

    /**
     * 矩形的宽
     */
    public int width;

    /**
     * 矩形的高
     */
    public int height;

    private Rect rect = new Rect();

    public AWRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AWRect(Rect rect) {
        this.x = rect.left;
        this.y = rect.top;
        this.width = (rect.right - rect.left);
        this.height = (rect.bottom - rect.top);
    }

    /**
     * 转成 Rect
     * @return
     */
    public Rect getRect() {
        rect.left = this.x;
        rect.top = this.y;
        rect.right = this.x + this.width;
        rect.bottom = this.y + this.height;
        return rect;
    }

    @Override
    public String toString() {
        return "AWRect{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
