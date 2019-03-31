package com.alanwang.aavlib.libvideo.camera;

import com.alanwang.aavlib.libmediacore.exception.AWException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 21:43.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraException extends AWException {

    public final static int ERROR_CAMERA_UNKNOWN_ERROR = 1001;
    public final static int ERROR_CAMERA_OPEN_FAILED = 1002;
    public final static int ERROR_CAMERA_SETTING_FAILED = 1003;
    public final static int ERROR_CAMERA_SET_EXPOSURE_FAILED = 1004;
    public final static int ERROR_CAMERA_SET_FLASH_LIGHT_FAILED = 1005;

    public AWCameraException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public AWCameraException(int errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }
}
