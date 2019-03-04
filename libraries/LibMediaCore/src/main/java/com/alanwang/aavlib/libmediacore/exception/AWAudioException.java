package com.alanwang.aavlib.libmediacore.exception;

/**
 * Author: AlanWang4523.
 * Date: 19/3/5 00:37.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioException extends AWMediaException {

    public AWAudioException(int errorCode) {
        super(errorCode);
    }

    public AWAudioException(String errorMsg) {
        super(errorMsg);
    }

    public AWAudioException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public AWAudioException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

    public AWAudioException(int errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }
}
