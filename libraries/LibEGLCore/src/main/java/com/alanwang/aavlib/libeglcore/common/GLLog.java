package com.alanwang.aavlib.libeglcore.common;

import android.util.Log;
import com.alanwang.aavlib.libeglcore.BuildConfig;

/**
 * Author: AlanWang4523.
 * Date: 19/3/28 00:54.
 * Mail: alanwang4523@gmail.com
 */
public class GLLog {
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void setDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

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
