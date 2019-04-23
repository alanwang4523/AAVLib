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
