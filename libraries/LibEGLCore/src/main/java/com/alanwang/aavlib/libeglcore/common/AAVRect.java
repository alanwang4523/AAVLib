package com.alanwang.aavlib.libeglcore.common;

import android.graphics.Rect;

/**
 * Author: AlanWang4523.
 * Date: 19/1/23 00:42.
 * Mail: alanwang4523@gmail.com
 */

public class AAVRect {
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

    public AAVRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AAVRect(Rect rect) {
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
        return "AAVRect{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
