package com.sanbafule.sharelock.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.chatting.help.ReceiveMessageHelp;
import com.sanbafule.sharelock.comm.SCode;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imlib.RongIMClient;


/***
 * 2016-10-8
 * ShareLock
 * 欢迎页面
 */
public class SplashActivity extends AppCompatActivity {
    //检测重新登录的字段
    private int isAgainLogin = 0;
    // 重新请求服务器的计数
    private int count = 0;

    // 重新请求三次检测重新登录的接口
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCode.AGAIN_LOGIN_CODE:
                    if (count > 2) {
                        try {
                            checkLogin();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        count++;
                    } else {
                        startLoginActivity();
                    }
                    break;
                case SCode.NOT_AGAIN_LOGIN_CODE:
                    if (isAgainLogin == 0) {
                        connt();
                    } else {
                        startLoginActivity();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView ig = (ImageView) findViewById(R.id.ig);
        Glide.with(this).load(R.drawable.timg).asGif().
                error(R.drawable.ic_play_gif).
                into(ig);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ShareLockManager.getInstance().isLogin()) {
                    try {
                        checkLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 1000);


    }

    /**
     * 跳转到登录界面
     */
    private void startLoginActivity() {
        clearUserInfo();
    }

    /**
     * 清除个人信息
     */
    private void clearUserInfo() {
        ShareLockManager.getInstance().clearUserInfo();
        startActivities(new Intent[]{new Intent(SplashActivity.this, MainActivity.class),
                new Intent(SplashActivity.this, LoginActivity.class)});
        finish();
    }

    /**
     * 检测登录
     *
     * @throws IOException
     */
    private void checkLogin() throws IOException {
        ArrayMap<String, String> map = new ArrayMap();
        map.put("loginVersion", String.valueOf(ShareLockManager.getInstance().getLoginVersion()));
        map.put("u_username", ShareLockManager.getInstance().getUserName());
        HttpBiz.httpPostBiz(Url.IS_AGAIN_LOGIN, map, new HttpInterface() {
            @Override
            public void onFailure() {
                // 重新请求
                handler.sendEmptyMessage(SCode.AGAIN_LOGIN_CODE);
            }

            @Override
            public void onSucceed(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    boolean isSuccess = SString.getSuccess(object);
                    final JSONObject result = SString.getResult(object);
                    if (isSuccess) {
                        isAgainLogin = result.getInt("isAgainLogin");
                    } else {
                        String message = SString.getMessage(object);
                        ToastUtil.showMessage(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 重新请求
                handler.sendEmptyMessage(SCode.NOT_AGAIN_LOGIN_CODE);
            }
        });

    }


    // 判断登录状态
    // 连接融云服务器
    private void connt() {
        if (ShareLockManager.getInstance().isLogin()) {
            RongIMClient.getInstance().disconnect();
            RongIMClient.connect(ShareLockManager.getInstance().getClentUser().getRongLibToken(), new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    // 重新获取token
                    getRongToken();

                }

                @Override
                public void onSuccess(String s) {
                    // 跳转到主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    // 注册消息接收器
                    RongIMClient.setOnReceiveMessageListener(ReceiveMessageHelp.getInstance(SApplication.getInstance()));
                    finish();

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    ToastUtil.showMessage("登录失败,请重新登录");
                }
            });
            //设置融云的连接状态
            RongIMClient.setConnectionStatusListener(ReceiveMessageHelp.getInstance(SApplication.getInstance()));
        }


    }

    private void getRongToken() {
        ArrayMap<String, String> map = new ArrayMap();
        map.put("userName", ShareLockManager.getInstance().getUserName());

        try {
            HttpBiz.httpPostBiz(Url.GET_RONG_TOKEN, map, new HttpInterface() {
                @Override
                public void onFailure() {

                    startLoginActivity();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            String token = SString.getResult(object).getString("rongLibToken");
                            ShareLockManager.getInstance().getClentUser().setRongLibToken(token);
                            ShareLockManager.getInstance().saveClientUser(ShareLockManager.getInstance().getClentUser());
                            connt();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}