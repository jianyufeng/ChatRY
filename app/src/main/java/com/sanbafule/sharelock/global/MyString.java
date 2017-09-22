package com.sanbafule.sharelock.global;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/4/29.
 */
public final class MyString {


    public static String subString(String url) {
        String a;
        return a = url.substring(url.lastIndexOf("."));
    }

    public static boolean hasData(String s) {

        if (s != null && !TextUtils.isEmpty(s)&&s.length()>0) {
            return true;
        }
        return false;
    }





}
