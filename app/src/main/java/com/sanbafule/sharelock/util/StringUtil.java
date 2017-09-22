package com.sanbafule.sharelock.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/28 14:54
 * cd : 三八妇乐
 * 描述：
 */
public class StringUtil {
    private StringUtil(){}
    public static SpannableString matcherSearchTitle( String text, String keyword) {
        int color=Color.parseColor("#f03791");
        String string = text.toLowerCase();
        String key = keyword.toLowerCase();
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(string);
        SpannableString ss = new SpannableString(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
}
