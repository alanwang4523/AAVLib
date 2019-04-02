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
