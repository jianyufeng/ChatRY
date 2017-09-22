package com.sanbafule.sharelock.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

/**
 * 作者:Created by 简玉锋 on 2017/1/6 15:00
 * 邮箱: jianyufeng@38.hn
 */

public class NotificationUtils {

    public static Notification buildNotification(Context context, int icon,
                                                 String tickerText,
                                                 String contentTitle, String contentText, Bitmap largeIcon,
                                                 PendingIntent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setShowWhen(true)
                .setSmallIcon(icon)
                .setContentIntent(intent);
        if (largeIcon != null) {
            mBuilder.setLargeIcon(largeIcon);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            //设置浮动
            mBuilder.setFullScreenIntent(intent, false);
        }
        Notification notification = mBuilder.build();
        // 发出提示音
        notification.defaults = Notification.DEFAULT_SOUND;
//        notification.sound = Uri.parse("file:/ sdcard /notification/ringer.mp3");
//        notification.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6");
        //手机振动
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        long[] vibrate = {0, 100, 200, 300};
        notification.vibrate = vibrate;
        //LED 灯闪烁，如：
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        return notification;
    }
}
