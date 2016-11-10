package org.kesar.lazy.lazydb.util;

import android.util.Log;

import static android.util.Log.getStackTraceString;

/**
 * log工具
 * Created by kesar on 16-11-10.
 */

public class LogUtils {
    public static boolean DEBUG = false;

    private LogUtils() {
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG)
            Log.i(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG)
            Log.i(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG)
            Log.i(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        if (DEBUG)
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG)
            Log.w(tag, msg, tr);
    }

    public static void w(String tag, Throwable tr) {
        if (DEBUG)
            Log.w(tag,tr);
    }

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG)
            Log.e(tag, msg, tr);
    }
}
