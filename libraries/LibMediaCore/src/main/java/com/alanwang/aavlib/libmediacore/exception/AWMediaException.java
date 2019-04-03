package com.alanwang.aavlib.libmediacore.exception;

/**
 * Author: AlanWang4523.
 * Date: 19/3/5 00:37.
 * Mail: alanwang4523@gmail.com
 */
public class AWMediaException extends AWException {

    public AWMediaException(int errorCode) {
        super(errorCode);
    }

    public AWMediaException(String errorMsg) {
        super(errorMsg);
    }

    public AWMediaException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public AWMediaException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

    public AWMediaException(int errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
    }
}
