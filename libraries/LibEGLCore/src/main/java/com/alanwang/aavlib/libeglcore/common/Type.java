package com.alanwang.aavlib.libeglcore.common;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: AlanWang4523.
 * Date: 19/3/27 00:30.
 * Mail: alanwang4523@gmail.com
 */
public class Type {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ScaleType.FIT_XY, ScaleType.CENTER_CROP})
    public @interface ScaleType {
        int FIT_XY = 0;
        int CENTER_CROP = 1;
    }
}
