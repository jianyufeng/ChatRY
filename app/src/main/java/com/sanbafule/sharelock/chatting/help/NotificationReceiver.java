package com.sanbafule.sharelock.chatting.help;

import android.content.Context;
import android.content.Intent;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.comm.SString;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 作者:Created by 简玉锋 on 2017/1/6 13:55
 * 邮箱: jianyufeng@38.hn
 */

public class NotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        String targetId = pushNotificationMessage.getTargetId();

        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SString.start_from, SString.notify_chat);
        context.startActivity(intent);
        return true;
    }
}
