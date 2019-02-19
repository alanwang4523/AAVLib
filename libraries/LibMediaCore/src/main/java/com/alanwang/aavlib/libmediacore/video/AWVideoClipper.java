package com.alanwang.aavlib.libmediacore.video;

import android.text.TextUtils;

import com.alanwang.aavlib.libmediacore.AWMediaClipper;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 01:41.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoClipper extends AWMediaClipper {

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
