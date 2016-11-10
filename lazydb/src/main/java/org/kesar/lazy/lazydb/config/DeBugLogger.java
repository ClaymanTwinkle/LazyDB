package org.kesar.lazy.lazydb.config;

import android.util.Log;

/**
 * 调试logger
 * Created by kesar on 2016/6/26 0026.
 */
public class DeBugLogger
{
    private static boolean DEBUG = false;

    public static void setDebug(boolean isDebug)
    {
        DeBugLogger.DEBUG = isDebug;
    }

    private DeBugLogger() {
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
