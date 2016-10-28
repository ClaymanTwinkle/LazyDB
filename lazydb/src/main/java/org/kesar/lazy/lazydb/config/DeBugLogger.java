package org.kesar.lazy.lazydb.config;

import android.util.Log;

/**
 * 调试logger
 * Created by kesar on 2016/6/26 0026.
 */
public class DeBugLogger
{
    private static final String TAG = "LazyDB";
    private static boolean isDebug = false;

    public static void setDebug(boolean isDebug)
    {
        DeBugLogger.isDebug = isDebug;
    }

    public static void d(String message)
    {
        if (isDebug)
            Log.d(TAG, message);
    }

    public static void d(String TAG, String message)
    {
        if (isDebug)
            Log.d(TAG, message);
    }

    public static void w(String message)
    {
        if (isDebug)
            Log.w(TAG, message);
    }

    public static void w(String TAG, String message)
    {
        if (isDebug)
            Log.w(TAG, message);
    }


    public static void e(String message)
    {
        if (isDebug)
            Log.e(TAG, message);
    }

    public static void e(String TAG, String message)
    {
        if (isDebug)
            Log.e(TAG, message);
    }
}
