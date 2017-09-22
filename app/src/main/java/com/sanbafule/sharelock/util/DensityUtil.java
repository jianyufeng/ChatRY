/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.sanbafule.sharelock.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.sanbafule.sharelock.SApplication;


/**
 * 2016-10-8
 * ShareLock
 * 像素单位的转化
 */
public class DensityUtil {

    // 根据屏幕密度转换
    private static float mPixels = 0.0F;
    private static float density = -1.0F;

    /**
     *
     * @param context
     * @param pixels
     * @return
     */
    public static int getDisplayMetrics(Context context, float pixels) {
        if (mPixels == 0.0F)
            mPixels = context.getResources().getDisplayMetrics().density;
        return (int) (0.5F + pixels * mPixels);
    }


    /**
     * 获取设备屏幕的宽
     * @param context
     * @return
     */
   public static int getDisplayWidth(Context context) {
       return    context.getResources().getDisplayMetrics().widthPixels;

    }

    /**
     *
     *获取屏幕的高
     * @param context
     * @return
     */
    public static int getDisplayHigth(Context context) {

       return   context.getResources().getDisplayMetrics().heightPixels;

    }
    

    public static int getImageWeidth(Context context , float pixels) {
        return context.getResources().getDisplayMetrics().widthPixels - 66 - getDisplayMetrics(context, pixels);
    }

    /**
     * 像素转化dip
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue){

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int)(pxValue / scale + 0.5f);

    }

    /**
     * dip转化像素
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);

    }

    /**
     * @param context
     * @param height
     * @return
     */
    public static int getMetricsDensity(Context context , float height) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(localDisplayMetrics);
        return Math.round(height * localDisplayMetrics.densityDpi / 160.0F);
    }


    public static int fromDPToPix(Context context, int dp) {
        return Math.round(getDensity(context) * dp);
    }

    /**
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        if (context == null)
            context = SApplication.getInstance().getApplicationContext();
        if (density < 0.0F)
            density = context.getResources().getDisplayMetrics().density;
        return density;
    }

    public static int round(Context context, int paramInt) {
        return Math.round(paramInt / getDensity(context));
    }

    public static  int getScWidth(Context context){
        int width=0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       return width = wm.getDefaultDisplay().getWidth();

    }
    public static  int getScHeight(Context context){
        int height=0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return height = wm.getDefaultDisplay().getHeight();
    }



    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     *  获取两点之间的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * 判断指定点是否在圆心之内
     * @param x1 当前点x坐标
     * @param y1 当前点y坐标
     * @param x2 圆心x坐标
     * @param y2 圆心y坐标
     * @param radius 圆的半径
     * @return
     */
    public static boolean isInCircle(float x1, float y1, float x2, float y2, int radius) {
        return distance(x1, y1, x2, y2) < radius;
    }

    /**
     * 获取状态栏高度＋标题栏高度
     *
     * @param activity
     * @return
     */
    public static int getTopBarHeight(Activity activity) {

        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        int titleBarHeight = actionBarHeight - statusBarHeight;
        return titleBarHeight;
    }
}
