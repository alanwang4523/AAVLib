package com.alanwang.aavlib.libvideo.common;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 19:55.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoSize {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Ratio.RATIO_4_3, Ratio.RATIO_16_9, Ratio.RATIO_1_1})
    public @interface Ratio {
        int RATIO_4_3 = 0;
        int RATIO_16_9 = 1;
        int RATIO_1_1 = 2;
    }

    public int width;
    public int height;

    public AWVideoSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void update(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getPixels() {
        return width * height;
    }

    /**
     * 获取VGA的大小
     * @param ratio
     * @return
     */
    public static AWVideoSize getVGASize(@Ratio int ratio) {
        if (ratio == Ratio.RATIO_16_9) {
            return new AWVideoSize(360, 640);
        } else {
            return new AWVideoSize(480, 640);
        }
    }

    /**
     * 获取纹理大小
     * @param ratio
     * @return
     */
    public static AWVideoSize getTextureSize(@Ratio int ratio) {
        if (ratio == Ratio.RATIO_16_9) {
            return new AWVideoSize(720, 1280);
        } else {
            return new AWVideoSize(960, 1280);
        }
    }

    /**
     * 获取期望的相机大小
     * @param ratio
     * @return
     */
    public static AWVideoSize getExpectCameraSize(@Ratio int ratio) {
        if (ratio == Ratio.RATIO_16_9) {
            return new AWVideoSize(1920, 1080);
        } else {
            return new AWVideoSize(1920, 1440);
        }
    }


    /**
     * 获取期望的相机大小
     * @param ratio
     * @return
     */
    public static AWVideoSize getDefaultCameraSize(@Ratio int ratio) {
        if (ratio == Ratio.RATIO_16_9) {
            return new AWVideoSize(640, 360);
        } else {
            return new AWVideoSize(640, 480);
        }
    }
}

