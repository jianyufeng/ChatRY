package com.sanbafule.sharelock.util;

import android.util.Log;



/**
 * Created by Administrator on 2016/8/5.
 */
public class LogUtil {



     // log tag的前缀可以为空 在这我使用了我的英文名
    public static final String TAG = "ShareLock";
    public static final String MSG = "log msg is null.";

    public static void v(String tag, String msg) {
        print(Log.VERBOSE, tag, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void d(String tag, String msg) {
        print(Log.DEBUG, tag, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void i(String tag, String msg) {
        print(Log.INFO, tag, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void w(String tag, String msg) {
        print(Log.WARN, tag, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void e(String tag, String msg) {
        print(Log.ERROR, tag, msg);
    }

    public static void e(String tag, String msg, RuntimeException e) {
        e(TAG, msg);
    }

    private static void print(int mode, final String tag, String msg) {
//        if (AppManager.isDebug) {
//            return;
//        }
        if (msg == null) {
            Log.e(tag, MSG);
            return;
        }
        switch (mode) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            default:
                Log.d(tag, msg);
                break;
        }
    }

    /***
     * 获得类名作为 tag
     * @param clazz 使用log的类对象
     * @return  String 一个字符串
     */
    public static  String getLogUtilsTag(Class<? extends Object> clazz) {
        return LogUtil.TAG + "." + clazz.getSimpleName();
    }
}
