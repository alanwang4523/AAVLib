package com.alanwang.aavlib.utils;

import android.util.Log;
import com.alanwang.aavlib.BuildConfig;

/**
 * Author: AlanWang4523.
 * Date: 19/1/24 01:03.
 * Mail: alanwang4523@gmail.com
 */

public class ALog {
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void i(String msg) {
        if (isDebug()) {
            Log.i(getCallerName(), msg);
        }
    }

    public static void d(String msg) {
        if (isDebug()) {
            Log.d(getCallerName(), msg);
        }
    }

    public static void v(String msg) {
        if (isDebug()) {
            Log.v(getCallerName(), msg);
        }
    }

    public static void e(String msg) {
        if (isDebug()) {
            Log.e(getCallerName(), msg);
        }
    }

    public static void e(Throwable e) {
        if (isDebug()) {
            Log.e(getCallerName(), "error", e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (isDebug()) {
            Log.e(getCallerName(), msg, e);
        }
    }

    public static void w(String msg) {
        if (isDebug()) {
            Log.w(getCallerName(), msg);
        }
    }

    private static String getCallerName() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        return elements[2].getClassName();
    }

    private static Boolean isDebug() {
        return DEBUG;
    }
}
