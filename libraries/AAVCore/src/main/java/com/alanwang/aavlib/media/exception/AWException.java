/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.media.exception;

/**
 * 自定义多媒体异常类，异常抛出后
 * 可通过 {@link #getErrorCode()} 获取错误码，
 * 可通过 {@link #getErrorMsg()} 获取错误信息
 * 
 * Author: AlanWang4523.
 * Date: 19/3/2 23:33.
 * Mail: alanwang4523@gmail.com
 */
public class AWException extends Exception {
    protected static final int DEFAULT_ERROR_CODE = -1;
    protected static final String DEFAULT_ERROR_MSG = "error";

    protected int errorCode;
    protected String errorMsg;

    public AWException(int errorCode) {
        this(errorCode, DEFAULT_ERROR_MSG);
    }

    public AWException(String errorMsg) {
        this(DEFAULT_ERROR_CODE, errorMsg);
    }

    public AWException(int errorCode, String errorMsg) {
        this(errorCode, errorMsg, null);
    }

    public AWException(String errorMsg, Throwable cause) {
        this(DEFAULT_ERROR_CODE, errorMsg, cause);
    }

    public AWException(int errorCode, String errorMsg, Throwable cause) {
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
