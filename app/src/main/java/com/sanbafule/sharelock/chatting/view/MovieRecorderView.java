package com.sanbafule.sharelock.chatting.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.comm.help.FileAccessor;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频播放控件
 *
 * @author liuyinjun
 * @date 2015-2-5
 */
/*      为了能够访问照相机，你必须在你的Android Manifest内声明CAMERA权限。同时确保包括了<uses-feature>节点元素，来声明你所使用的camera功能。例如，如果你使用了照相机的自动对焦功能，你的Manifest应当包括以下内容：

<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
         用该类拍照的执行步骤：
         1. 通过open(int)函数，获得一个Camera实例。
         2. 通过getParameters()函数，获得已有的设置（默认）。
         3. 如果需要的话，调用setParameters(Camera.Parameters)函数，修改第二步返回的Camera.Parameters对象。
         4. 如果需要的话，调用setDisplayOrientation(int)函数，设置屏幕水平或垂直。
         5. 重要：传递一个已初始化好的SurfaceHolder对象给setPreviewDisplay(SurfaceHolder)函数。如果没有surface，camera将无法打开预览。
         6. 重要：调用startPreview()函数开始更新预览surface。预览必须在你照相前启动。
         7. 在你想要捕获一张照片时，调用takePicture(Camera.ShutterCallback, Camera.PictureCallback, Camera.PictureCallback, Camera.PictureCallback)函数。等待回调函数中给出的实际图像数据。
         8. 在照相后，预览显示将会停止。如果要照更多相片，需要再次调用startPreview()函数（回至第六步）。
         9. 调用stopPreview()函数，停止更新预览surface。
         10. 重要：调用release()函数，释放camera引用，以便其他应用使用。应用应当在onPause()调用时立刻释放camera引用（在onResume()时重新打开它）。

         快速转换到视频录像模式，用以下步骤：
         1. 如上所述，获取一个初始化好的Camera对象并开启预览。
         2. 调用unlock()函数，以允许media进程得以访问camera。
         3. 传递当前camera对象至setCamera(Camera)函数。请查看MediaRecorder关于视频录制的信息。
         4. 当结束录制时，调用reconnect()函数重新取的和加锁camera对象。
         5. 如上所述，调用stopPreview()和release()函数，结束拍摄。

         这个类是线程不安全的，意味着是通过event线程使用的该类。大多数长时间运行的操作（预览、聚焦、拍照等）都是非同步的，并且是在需要的时候才会回调。而这些回调函数是在event线程open(int)函数被调用时触发的。所以，该类的方法一定不能在多线程内调用。
         警告：不同的Android设备有不同的硬件规格，如兆像素等级和自动对焦性能。为了使你的应用和更多设备兼容，最好不要限制camera规格。*/
    /*2）自定义摄像
2.1）流程简述
         Android框架的视频捕捉需要对Camera对象进行仔细的管理，还要与MediaRecorder类一起协同工作。使用Camera录制视频时，必须管理好Camera.lock()与Camera.unlock()的调用，使得MediaRecorder能够顺利访问摄像头硬件，并且还要进行 Camera.open()和Camera.release()调用。
         注意：自Android 4.0 (API level 14) 开始，Camera.lock()和 Camera.unlock()调用由系统自动管理。
         与用摄像头拍照不同，视频捕获必需十分精确地按顺序进行调用。必须按照特定的顺序来执行，应用程序才能成功地准备并捕获视频，详细步骤如下。
1. 打开摄像头——用Camera.open()来获得一个camera对象的实例。
2. 连接预览——用Camera.setPreviewDisplay()将camera连接到一个SurfaceView，准备实时预览。
3. 开始预览——调用 Camera.startPreview()开始显示实时摄像画面。
4. 开始录制视频——严格按照以下顺序执行才能成功录制视频：
         a. 解锁Camera——调用Camera.unlock()解锁，便于MediaRecorder使用摄像头。
         b. 配置MediaRecorder——按照如下顺序调用MediaRecorder中的方法。详情请参阅MediaRecorder参考文档。
                  1. setCamera()——用当前Camera实例将摄像头用途设置为视频捕捉。
                  2. setAudioSource()——用MediaRecorder.AudioSource.CAMCORDER设置音频源。
                  3. setVideoSource()——用MediaRecorder.VideoSource.CAMERA设置视频源。
                  4. 设置视频输出格式和编码格式。对于Android 2.2 (API Level 8) 以上版本，使用MediaRecorder.setProfile方法，并用CamcorderProfile.get()来获取一个 profile实例。对于Android prior to 2.2以上版本，必须设置视频输出格式和编码参数：
                           i. setOutputFormat()——设置输出格式，指定缺省设置或MediaRecorder.OutputFormat.MPEG_4。
                           ii. setAudioEncoder()——设置声音编码类型。指定缺省设置或MediaRecorder.AudioEncoder.AMR_NB。
                           iii. setVideoEncoder()——设置视频编码类型，指定缺省设置或者 MediaRecorder.VideoEncoder.MPEG_4_SP。
                  5. setOutputFile()——用getOutputMediaFile(MEDIA_TYPE_VIDEO).toString()设置输出文件，见保存媒体文件一节中的方法示例。
                  6. setPreviewDisplay()——用上面连接预览中设置的对象来指定应用程序的SurfaceView预览layout元素。
                  警告： 必须按照如下顺序调用MediaRecorder的下列配置方法，否则应用程序将会引发错误，录像也将失败。
         c. 准备MediaRecorder——调用MediaRecorder.prepare()设置配置，准备好MediaRecorder。
         d. 启动MediaRecorder——调用MediaRecorder.start()开始录制视频。
5. 停止录制视频——按照顺序调用以下方法，才能成功完成视频录制：
         a. 停止MediaRecorder——调用MediaRecorder.stop()停止录制视频。
         b. 重置MediaRecorder——这是可选步骤，调用MediaRecorder.reset()删除recorder中的配置信息。
         c. 释放MediaRecorder——调用MediaRecorder.release()释放MediaRecorder。
         d. 锁定摄像头——用Camera.lock()锁定摄像头，使得以后MediaRecorder session能够使用它。自Android 4.0 (API level 14)开始，不再需要本调用了，除非MediaRecorder.prepare()调用失败。
6. 停止预览——activity使用完摄像头后，应用Camera.stopPreview()停止预览。
7. 释放摄像头——使用Camera.release()释放摄像头，使其它应用程序可以使用它。
注意： 也可以不必先创建摄像头预览就使用MediaRecorder，并跳过本节开始的几步。不过，因为用户一般都希望在开始录像前看到预览画面，这里就不讨论那类过程了。

         摘录自：后记->参阅内容1

2.2）摄像预览
         摄像头预览类，用于嵌入一个View布局中。其实现了SurfaceHolder.Callback接口来捕捉view创建和销毁时的回调事件。

2.3）摄像活动
         按官方开发者指南里的流程实现的（参阅2.1流程简述）。但这不同版本和硬件下有些问题==，在代码里有稍带说明。
         ps：至于想要实现一个优秀的Camera摄像，请看后记->参阅内容2。

2.4）摄像调用
         以startActivity (…)方式跳转入摄像Activity即可。当然也可以用startActivityForResult(…)，在摄像完成后setResult(…)返回^^。*/
public class MovieRecorderView extends LinearLayout implements OnErrorListener {

    private static final String TAG = "MovieRecorderView";
    //显示预览
    private SurfaceView mSurfaceView;
    //holder
    private SurfaceHolder mSurfaceHolder;
    //进度
    public RoundProgressBar mProgressBar;
    //录制
    private MediaRecorder mMediaRecorder;
    //照相机
    private Camera mCamera;
    //计时器
    private Timer mTimer;// 计时器
    // 录制完成回调接口
    private OnRecordFinishListener mOnRecordFinishListener;

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度

    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件

    //    private Camera.AutoFocusCallback mFocusCallback = new Camera.AutoFocusCallback() {
//        @Override
//        public void onAutoFocus(boolean success, Camera camera) {
//            if (success){
//                ToastUtil.showMessage("success");
//            }else {
//                ToastUtil.showMessage("fail");
//
//            }
//        }
//    };
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化各项组件
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);

//
//        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
//
//        context. getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
//
//        int W = mDisplayMetrics.widthPixels;
//        int H = mDisplayMetrics.heightPixels;
        mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 640);// 默认320
        mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 480);// 默认240

        isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// 默认为10

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (RoundProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        a.recycle();
    }

    /**
     * @date 2015-2-5
     */
    private class CustomCallBack implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }

    }

    public void switchCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    break;
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (cameraId != Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    break;
                }
            }
        }
        try {
            initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void initCamera() throws IOException {
        if (mCamera != null) {
            //如果已有 mCamera  则需要对释放 及 做些准备的动作
            freeCameraResource();
        }
        try {

            mCamera = Camera.open(cameraId);

        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }

        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(mWidth, mHeight, parameters);
        if (size != null) {
            mWidth = size.width;
            mHeight = size.height;
        }
        parameters.setPreviewSize(mWidth, mHeight);


        parameters.setPreviewFrameRate(20);
        //设置自动聚焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setDisplayOrientation(90);
        //绑定预览的 SurfaceView
        mCamera.setParameters(parameters);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        //开始预览
        mCamera.startPreview();
        //Camera::unLock操作解锁摄像头，因为默认Camera都是锁定的，只有解锁后MediaRecorder等多媒体进程调用
        mCamera.unlock();

    }

    /**
     * 释放摄像头资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */

    private void freeCameraResource() {
        if (mCamera != null) {
            //设置预览监听
            mCamera.setPreviewCallback(null);
            //设置预览
            //调用startPreview()函数开始更新预览surface。预览必须在你照相前启动。
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
            isOpenCamera = false;
        }
    }

    /***
     * 创建文件夹的路径
     */
    private void createRecordDir() {
        File sampleDir = new File(FileAccessor.getChattingVideoPath(ShareLockManager.getInstance().getUserName()));
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        // 创建文件
        try {
            mRecordFile = File.createTempFile("recording", ".mp4", sampleDir); //mp4格式
            Log.i(TAG, mRecordFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 录制参数
     *
     * @throws IOException
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void initRecord(int width, int height) throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null) {
            //设置一个recording的摄像头
            mMediaRecorder.setCamera(mCamera);
        }
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        // 视频源  // 设置从摄像头采集图像
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);
        // 设置用于录制的音源。  // 设置从麦克风采集声音
        mMediaRecorder.setAudioSource(AudioSource.MIC);
        //  设置在录制过程中产生的输出文件的格式  // 设置视频的输出格式 为MP4
        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
        // 设置audio的编码格式  // 设置音频的编码格式
        mMediaRecorder.setAudioEncoder(AudioEncoder.DEFAULT);
        // 视频录制格式  // 设置视频的编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置默认分辨率：// 设置视频大小
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoSize(mWidth, mHeight);
        // 设置要捕获的视频帧速率  // 设置帧率
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoFrameRate(30);
        // 设置录制的视频编码比特率，然后就清晰了
        mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        // 输出旋转90度，保持竖屏录制
       /* 2.1竖屏情况下：
        如果是前置摄像头：
        mediaRecorder.setOrientationHint(270);
        如果是后置摄像头：
        mediaRecorder.setOrientationHint(90);
        2.2横情况下：
         如果是前置摄像头：
        mediaRecorder.setOrientationHint(180);
        如果是后置摄像头：
        mediaRecorder.setOrientationHint(0); */
        mMediaRecorder.setOrientationHint(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? 90 : 270);

        //设置视频存储路径    设置输出文件的路径
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());//设置视频存储路径
        mMediaRecorder.prepare();
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.start();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                ToastUtil.showMessage(e.getMessage());
            }
        }
    }

    //获取分辨率
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 开始录制视频
     *
     * @param
     * @param onRecordFinishListener 达到指定时间之后回调接口
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void record(int width, int height, final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        /***
         * 创建文件夹
         */
        createRecordDir();
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            /***
             * 初始化录制参数
             */
            initRecord(width, height);
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount > mRecordMaxTime) {// 达到指定时间，停止拍摄
//                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 1000, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    /**
     * 释放资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

    /**
     * 录制完成回调接口
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        ToastUtil.showMessage("错误");
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}