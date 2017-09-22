package com.sanbafule.sharelock.view.BottomTab;

import android.content.Context;
import android.content.res.TypedArray;

import com.sanbafule.sharelock.R;


/**
 * User:Shine
 * Date:2015-11-20
 * Description:
 */
public class ThemeUtils {

    private static final int[] APPCOMPAT_CHECK_ATTRS = {R.attr.colorPrimary};

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme ");
        }
    }
}