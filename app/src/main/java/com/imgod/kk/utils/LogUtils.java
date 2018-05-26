package com.imgod.kk.utils;

import android.util.Log;

import com.imgod.kk.BuildConfig;

/**
 * @author imgod1
 * @version 2.0.0 2018/5/26 14:42
 * @update imgod1 2018/5/26 14:42
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class LogUtils {
    public static boolean debug = true;

    public static void e(String tag, String content) {
        if (BuildConfig.DEBUG || debug) {
            Log.e(tag, content);
        }
    }
}
