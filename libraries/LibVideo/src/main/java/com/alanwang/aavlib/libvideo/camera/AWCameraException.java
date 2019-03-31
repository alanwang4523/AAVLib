package com.alanwang.aavlib.libvideo.camera;

import com.alanwang.aavlib.libmediacore.exception.AWException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 21:43.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraException extends AWException {

    public AWCameraException(int errorCode) {
        super(errorCode);
    }

    public AWCameraException(String errorMsg) {
        super(errorMsg);
    }

    public AWCameraException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public AWCameraException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

    public AWCameraException(int errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }
}
