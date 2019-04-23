/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 00:58.
 * Mail: alanwang4523@gmail.com
 */

public class AWMessage {

    public final static int MSG_DRAW = 10001;

    public final static int MSG_CAMERA_SWITCH = 10101;// 切换摄像头
    public final static int MSG_CAMERA_TOGGLE_FLASH_LIGHT = 10102;// 开关闪光灯

    public final int msgWhat;
    public int msgArg1;
    public int msgArg2;
    public Object msgObj;

    public AWMessage(int msgWhat) {
        this(msgWhat, 0, 0, null);
    }

    public AWMessage(int msgWhat, int msgArg1) {
        this(msgWhat, msgArg1, 0, null);
    }

    public AWMessage(int msgWhat, Object msgObj) {
        this(msgWhat, 0, 0, msgObj);
    }

    public AWMessage(int msgWhat, int msgArg1, int msgArg2, Object msgObj) {
        this.msgWhat = msgWhat;
        this.msgArg1 = msgArg1;
        this.msgArg2 = msgArg2;
        this.msgObj = msgObj;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("AWMessage:: msgWhat = ").append(msgWhat)
                .append(", msgArg1 = ").append(msgArg1)
                .append(", msgArg2 = ").append(msgArg2)
                .append(", msgObj = ").append(msgObj);
        return strb.toString();
    }
}
