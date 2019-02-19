package com.alanwang.aavlib.libmediacore.audio;

import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.AWMediaExtractor;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 00:52.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioExtractor extends AWMediaExtractor {

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("audio")) {
            return true;
        } else {
            return false;
        }
    }
}
