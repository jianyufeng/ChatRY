package com.sanbafule.sharelock.chatting.activity.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.view.RoundProgressBar;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.message.FileMessage;
import io.rong.message.utils.BitmapUtil;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by Administrator on 2016/7/1.
 * 视频播放界面
 */
public class PlayVideoActivity extends FragmentActivity implements Runnable, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    //视频的地址
    private String path;
    //视频播放载体
    private VideoView mVideoView;
    //播放控制器
    private MyMediaController mediaController;
    //预览的图片
    private ImageView mPrewView;

    private static final int TIME = 0;
    private static final int BATTERY = 1;
    //下载进度
    private RoundProgressBar progressBar;

    private boolean isDownLoad = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME:
                    mediaController.setTime(msg.obj.toString());
                    break;
                case BATTERY:
                    mediaController.setBattery(msg.obj.toString());
                    break;
            }
        }
    };


    public void toggleHideyBar() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
//            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
//            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {//隐藏虚拟键，点击可出现
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {//全屏模式：隐藏状态栏，但并不隐藏虚拟键
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        super.onCreate(savedInstanceState);
        //完全全屏
        toggleHideyBar();
        setContentView(R.layout.activity_paly_vedio);
        initView(); //初始化控件
        initData();//初始化视频路径


    }

    private void playVideo() {
        //实例化控制器
        mediaController = new MyMediaController(this, mVideoView, this);
        //控制器显示5s后自动隐藏 显示MediaController。默认显示3秒后自动隐藏
//        mediaController.show(5000);
        //设置媒体播放器。并更新播放/暂停按钮状态
//        mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
//            @Override
//            public void start() {
//                ToastUtil.showMessage("start");
//            }
//
//            @Override
//            public void pause() {
//                ToastUtil.showMessage("pause");
//
//            }
//
//            @Override
//            public long getDuration() {
//                ToastUtil.showMessage("getDuration");
//
//                return 0;
//            }
//
//            @Override
//            public long getCurrentPosition() {
//                ToastUtil.showMessage("getCurrentPosition");
//
//                return 0;
//            }
//
//            @Override
//            public void seekTo(long pos) {
//                ToastUtil.showMessage("getCurrentPosition");
//
//
//            }
//
//            @Override
//            public boolean isPlaying() {
//                ToastUtil.showMessage("getCurrentPosition");
//
//                return false;
//            }
//
//            @Override
//            public int getBufferPercentage() {
//                ToastUtil.showMessage("getCurrentPosition");
//
//                return 0;
//            }
//        });
        //设置用户拖拽SeekBar时画面是否跟着变化。（VPlayer默认完成操作后再更新画面）
//        mediaController.setInstantSeeking(true);
        //设置视频文件名称
//        mediaController.setFileName("");
        // 更改MediaController的动画风格。
        // 如果MediaController正在显示，调用此方法将在下一次显示时生效。
        // 参数 animationStyle 在MediaController显示或隐藏时使用的动画风格。设置-1为默认风格，0没有动画，或设置一个明确的动画资源。
//        mediaController.setAnimationStyle(int animationStyle)
        //注册一个回调函数，在MediaController显示后被调用
//        mediaController.setOnShownListener()
        //注册一个回调函数，在MediaController隐藏后被调用
//        mediaController.setOnHiddenListener()
        //设置播放画质 高画质
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        //绑定控制器
        mVideoView.setMediaController(mediaController);
        mVideoView.setOnPreparedListener(this);
        ////设置播放地址
        mVideoView.setVideoPath(path);
        //设置重复播放
        mVideoView.setOnCompletionListener(this);
        //设置播放出错是的监听
        mVideoView.setOnErrorListener(this);
        registerBoradcastReceiver();
        new Thread(this).start();

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (sendTime) {
            //时间读取线程
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String str = sdf.format(new Date());
            Message msg = new Message();
            msg.obj = str;
            msg.what = TIME;
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void registerBoradcastReceiver() {
        //注册电量广播监听
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);
                //把它转成百分比
                //tv.setText("电池电量为"+((level*100)/scale)+"%");
                Message msg = new Message();
                msg.obj = (level * 100) / scale + "";
                msg.what = BATTERY;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private boolean sendTime = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendTime = false;
        unregisterReceiver(batteryBroadcastReceiver);
        mVideoView.stopPlayback();
        mVideoView = null;
        if (isDownLoad) {
            RongIMClient.getInstance().cancelDownloadMediaMessage(message, null);
        }
    }


    private void initView() {
        //点击播放按钮
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mPrewView = (ImageView) findViewById(R.id.preview_view);
        mVideoView.setHardwareDecoder(true);
        progressBar = (RoundProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
    }

    @Override

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        String message = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "屏幕设置为：横屏" : "屏幕设置为：竖屏";
        ToastUtil.showMessage(message);

    }


    private io.rong.imlib.model.Message message;

    private void initData() {
        Intent intent = getIntent();
        path = intent.getStringExtra(SString.VIDEO_PATH);
        if (TextUtils.isEmpty(path)) {
            message = intent.getParcelableExtra(SString.TYPE);
            if (message == null) {
                finish();
                return;
            }
            FileMessage content = (FileMessage) message.getContent();
            mPrewView.setImageBitmap(BitmapUtil.getBitmapFromBase64(content.getExtra()));
            Uri localPath = content.getLocalPath();
            if (localPath == null) {
                downLoadVideo();
            } else {
                path = localPath.getPath();
                if (!new File(path).exists()) {
                    downLoadVideo();
                } else {
                    progressBar.setVisibility(View.GONE);
                    playVideo();
                }
            }


        } else {
            progressBar.setVisibility(View.GONE);
            String img = intent.getStringExtra("img");
            if (!TextUtils.isEmpty(img)) {
                mPrewView.setImageBitmap(BitmapUtil.getBitmapFromBase64(img));
            }
            playVideo();
        }
    }

    private void downLoadVideo() {
        isDownLoad = true;
        progressBar.setVisibility(View.VISIBLE);
        RongIMClient.getInstance().downloadMediaMessage(message, new IRongCallback.IDownloadMediaMessageCallback() {
            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                isDownLoad = false;
                progressBar.setVisibility(View.GONE);
                FileMessage content = (FileMessage) message.getContent();
                path = content.getLocalPath().getPath();
                playVideo();
            }

            @Override
            public void onProgress(io.rong.imlib.model.Message message, int i) {
                progressBar.setProgress(i);
            }

            @Override
            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                isDownLoad = false;
                ToastUtil.showMessage("下载出错");
            }

            @Override
            public void onCanceled(io.rong.imlib.model.Message message) {
                ToastUtil.showMessage("下载取消");
            }
        });
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mPrewView.setVisibility(View.GONE);
        mVideoView.setVolume(0f,0f);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        ToastUtil.showMessage("播放出错");
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mVideoView.setVideoPath(path);
        registerBoradcastReceiver();
    }
}
