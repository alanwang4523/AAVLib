package com.alanwang.aavlib.libutils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: AlanWang4523.
 * Date: 19/4/10 00:58.
 * Mail: alanwang4523@gmail.com
 */
public class TimeUtils {
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 将当前时间转换成相应的格式
     *
     * @return
     */
    public static String getCurrentTime() {
        return sFormatter.format(new Date());
    }

    /**
     * 将毫秒转化成hh:mm:ss字符串
     *
     * @param ms
     * @return
     */
    public static String getFormatTime(final long ms) {
        if (ms <= 1000) {
            return "00:00";
        }
        long seconds = ms / 1000;
        long min = seconds % 60;
        seconds /= 60;
        String strs = min < 10 ? "0" + min : "" + min;
        if (60 > seconds) {
            String strm = seconds < 10 ? "0" + seconds : "" + seconds;
            return strm + ":" + strs;
        }
        long m = seconds % 60;
        seconds /= 60;
        String strm = m < 10 ? "0" + m : "" + m;
        String strh = seconds < 10 ? "0" + seconds : "" + seconds;
        return strh + ":" + strm + ":" + strs;
    }
}
