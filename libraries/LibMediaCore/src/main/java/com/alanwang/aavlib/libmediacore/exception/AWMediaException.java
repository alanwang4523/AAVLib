package com.alanwang.aavlib.libmediacore.exception;

/**
 * 自定义多媒体异常类，异常抛出后
 * 可通过 {@link #getErrorCode()} 获取错误码，
 * 可通过 {@link #getErrorMsg()} 获取错误信息
 * 
 * Author: AlanWang4523.
 * Date: 19/3/2 23:33.
 * Mail: alanwang4523@gmail.com
 */
public class AWMediaException extends Exception {

    protected int errorCode;
    protected String errorMsg;

    public AWMediaException(int errorCode) {
        this(errorCode, "");
    }

    public AWMediaException(int errorCode, String errorMsg) {
        this(errorCode, errorMsg, null);
    }

    public AWMediaException(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        StringBuilder strBuilder = new StringBuilder(s);
        strBuilder.append(": ")
                .append("errorCode: ").append(errorCode)
                .append(", errorMsg: ").append(getLocalizedMessage());
        return strBuilder.toString();
    }
}