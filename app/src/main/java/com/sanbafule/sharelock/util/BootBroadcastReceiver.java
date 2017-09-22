package com.sanbafule.sharelock.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 作者:Created by 简玉锋 on 2017/1/12 13:43
 * 邮箱: jianyufeng@38.hn
 * 开机自动服务自动启动
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
//        Intent service = new Intent(context, RongService.class);
//        context.startService(service);
        Log.v("TAG", "开机自动服务自动启动.....");
        //启动应用，参数为需要自动启动的应用的包名
        Intent i =context.getPackageManager().getLaunchIntentForPackage("com.sanbafule.sharelock");
        context.startActivity(i);
    }
}
