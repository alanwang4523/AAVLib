package com.alanwang.aavlib.libmediacore.video;

import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.AWMediaExtractor;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 00:42.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoExtractor extends AWMediaExtractor {

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }
}
