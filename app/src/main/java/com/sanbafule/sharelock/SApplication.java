package com.sanbafule.sharelock;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.rong.imlib.RongIMClient;
import io.rong.push.RongPushClient;
import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/10/8.
 */
public class SApplication extends Application {

    private static SApplication instance;
    /**
     * 单例，返回一个实例
     * @return
     */
    public static SApplication getInstance() {
        if (instance == null) {
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        Vitamio.isInitialized(this);
    }

    public  void  init(){
    /**
     * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
     * io.rong.push 为融云 push 进程名称，不可修改。
     */
    if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
            "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
        RongPushClient.registerMiPush(this, "2882303761517545506", "5811754544506");
        RongIMClient.init(this);
    }
}
    //获取当前调用的进程
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}