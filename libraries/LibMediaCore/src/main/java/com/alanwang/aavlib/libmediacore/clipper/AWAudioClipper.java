package com.alanwang.aavlib.libmediacore.clipper;

import android.text.TextUtils;

import com.alanwang.aavlib.libmediacore.clipper.AWAbstractAVClipper;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 01:40.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioClipper extends AWAbstractAVClipper {

    public AWAudioClipper(String outputPath) {
        super(outputPath);
    }

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("audio")) {
            return true;
        } else {
            return false;
        }
    }
}
