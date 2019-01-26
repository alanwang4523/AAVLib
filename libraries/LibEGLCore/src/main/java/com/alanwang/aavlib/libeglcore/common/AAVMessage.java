package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 00:58.
 * Mail: alanwang4523@gmail.com
 */

public class AAVMessage {
    public final int msgWhat;
    public int msgArg1;
    public int msgArg2;
    public Object msgObj;

    public AAVMessage(int msgWhat) {
        this(msgWhat, 0, 0, null);
    }

    public AAVMessage(int msgWhat, Object msgObj) {
        this(msgWhat, 0, 0, msgObj);
    }

    public AAVMessage(int msgWhat, int msgArg1, int msgArg2, Object msgObj) {
        this.msgWhat = msgWhat;
        this.msgArg1 = msgArg1;
        this.msgArg2 = msgArg2;
        this.msgObj = msgObj;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("AAVMessage:: msgWhat = ").append(msgWhat)
                .append(", msgArg1 = ").append(msgArg1)
                .append(", msgArg2 = ").append(msgArg2)
                .append(", msgObj = ").append(msgObj);
        return strb.toString();
    }
}
