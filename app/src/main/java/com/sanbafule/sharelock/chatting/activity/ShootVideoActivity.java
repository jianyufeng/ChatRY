package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.play.PlayVideoActivity;
import com.sanbafule.sharelock.chatting.view.MovieRecorderView;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.ToastUtil;


/***
 * 录制小视屏的页面
 */
public class ShootVideoActivity extends FragmentActivity implements View.OnClickListener {
    //录制
    private MovieRecorderView mRecorderView;
    //开始录制
//    private Button mShootBtn;
    //点击播放
    private ImageView mPlayButton;
    //摄像头转换
    private ImageView switchCamera;
    //是否完成录制
    private boolean isFinish = true;
    //宽度
    private int width;
    //高度
    private int height;

    private Button send;
    private Button cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_shoot_viewo);
        int numberOfCameras = Camera.getNumberOfCameras();//照相机的个数  分为前置 后置
        if (numberOfCameras < 0) {
            finish();
            ToastUtil.showMessage("该手机没有摄像功能");
            return;
        }
        //视频录制控件
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
//        //点击开始录制
//        mShootBtn = (Button) findViewById(R.id.shoot_button);
        //点击播放
        mPlayButton = (ImageView) findViewById(R.id.img_play);
        mPlayButton.setOnClickListener(this);

        mRecorderView.mProgressBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    deleteVideo();
                    setVisiable(false);
                    mRecorderView.record(width, height, new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            isFinish = true;
                            ToastUtil.showMessage("录制完成");
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRecorderView.getTimeCount() > 1) {
                        handler.sendEmptyMessage(1);
                    } else {
                        deleteVideo();
                        mRecorderView.stop();
                        Toast.makeText(ShootVideoActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        switchCamera = (ImageView) findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }



    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isFinish) {
                mRecorderView.stop();
                setVisiable(true);
            }
        }
    };

    private void finishActivity() {
        // 返回到播放页面
        Intent intent = new Intent();
//        Log.d(TAG, mRecorderView.getmRecordFile().getAbsolutePath());
//        intent.putExtra("url", mRecorderView.getmRecordFile().getName());
        intent.putExtra(SString.VIDEO_PATH, mRecorderView.getmRecordFile().getAbsolutePath());
        setResult(RESULT_OK, intent);
        // isFinish = false;
        finish();
    }
    private void deleteVideo(){
        if (mRecorderView.getmRecordFile() != null) {
            mRecorderView.getmRecordFile().delete();
        }
    }
    private void setVisiable(boolean isFinish){
        mPlayButton.setVisibility(isFinish?View.VISIBLE:View.GONE);
        send.setVisibility(isFinish?View.VISIBLE:View.GONE);
        cancel.setVisibility(isFinish?View.VISIBLE:View.GONE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {// 回退
            case R.id.cancel:
                deleteVideo();
                finish();
                break;
            case R.id.send://发送
                finishActivity();
                break;

            case R.id.img_play:// 播放视频
                if (MyString.hasData(mRecorderView.getmRecordFile().getAbsolutePath())) {
                    Intent intent = new Intent(this, PlayVideoActivity.class);
                    intent.putExtra(SString.VIDEO_PATH, mRecorderView.getmRecordFile().getAbsolutePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "文件不存在无法播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_camera: //切换镜头
                mRecorderView.switchCamera();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteVideo();
    }
}
