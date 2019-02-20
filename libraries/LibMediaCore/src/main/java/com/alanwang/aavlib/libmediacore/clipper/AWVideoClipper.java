package com.alanwang.aavlib.libmediacore.clipper;

import android.text.TextUtils;

import com.alanwang.aavlib.libmediacore.clipper.AWAbstractAVClipper;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 01:41.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoClipper extends AWAbstractAVClipper {

    public AWVideoClipper(String outputPath) {
        super(outputPath);
    }

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }
}
