
package com.sanbafule.sharelock.zxing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.zxing.camera.BeepManager;
import com.sanbafule.sharelock.zxing.camera.CameraManager;
import com.sanbafule.sharelock.zxing.decode.CaptureActivityHandler;
import com.sanbafule.sharelock.zxing.decode.DecodeThread;
import com.sanbafule.sharelock.zxing.decode.InactivityTimer;
import com.sanbafule.sharelock.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * 条码二维码扫描功能实现
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 0x051;

    private boolean hasSurface;
    private BeepManager beepManager;// 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
    public SharedPreferences mSharedPreferences;// 存储二维码条形码选择的状态
    public static String currentState;// 条形码二维码选择状态
    private String characterSet;

    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private TextView statusView;
    private TextView scanTextView;
    private View resultView;
    private ImageView onecode;
    private ImageView qrcode;
//    private TopBarView topBarVie;
    private TextView qrcode_tv_id, tiao_ma_tv_Id;
    public static CaptureActivity mInstance;
//    private TextView net_statu;
    /**
     * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
     * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
     */
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private Vector<BarcodeFormat> decodeFormats;// 编码格式
    private CaptureActivityHandler mHandler;// 解码线程

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
            .of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    private String photo_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        initSetting();
        setContentView(R.layout.activity_capture);
        initComponent();
        initView();
        initEvent();
    }

    /**
     * 初始化窗口设置
     */
    private void initSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
        // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
    }

    /**
     * 初始化功能组件
     */
    private void initComponent() {
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        currentState = this.mSharedPreferences.getString("currentState",
                "qrcode");
        cameraManager = new CameraManager(getApplication());
    }

    /**
     * 初始化视图
     */
    private void initView() {

        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
//        resultView = findViewById(R.id.result_view);
//		scanTextView = (TextView) findViewById(R.id.mtextview_title);
        statusView = (TextView) findViewById(R.id.status_view);
        onecode = (ImageView) findViewById(R.id.onecode_id);
        qrcode = (ImageView) findViewById(R.id.qrcode_id);
        qrcode.setBackgroundResource(R.drawable.scan_mode_qr);
//        topBarVie = (TopBarView) findViewById(R.id.topbar);
//        topBarVie.setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, "二维码/条形码", this);
//        topBarVie.setTopBarToStatus(1, R.drawable.topbar_back_bt, "相册", "二维码/条形码", this);
        qrcode_tv_id = (TextView) findViewById(R.id.qrcode_tv_id);
        tiao_ma_tv_Id = (TextView) findViewById(R.id.tiao_ma_tv_Id);
//        net_statu=(TextView) findViewById(R.id.net_statu);
    }

    /**
     * 初始化点击切换扫描类型事件
     */
    private void initEvent() {
        onecode.setOnClickListener(this.onecodeImageListener);
        qrcode.setOnClickListener(this.qrcodeImageListener);
        qrcode.setSelected(true);
    }

    /**
     * 初始设置扫描类型（最后一次使用类型）
     */
    private void setScanType() {
        do {
            if ((CaptureActivity.currentState != null)
                    && (CaptureActivity.currentState.equals("onecode"))) {
                qrcode.setBackgroundResource(R.drawable.android_saoyisao_qr_pinkcodehl);
                onecode.setBackgroundResource(R.drawable.android_saoyisao_barcode_select);
                tiao_ma_tv_Id.setTextColor(getResources().getColor(R.color.android_fen_color));
                qrcode_tv_id.setTextColor(getResources().getColor(R.color.android_ciyao_text_color));
                qrcode.setSelected(false);
                onecode.setSelected(true);
                viewfinderView.setVisibility(View.VISIBLE);
                onecodeSetting();
                statusView.setText(R.string.scan_onecode);
                return;
            }
        }

        while ((CaptureActivity.currentState == null)
                || (!CaptureActivity.currentState.equals("qrcode")));
        onecode.setBackgroundResource(R.drawable.android_saoyisao_barcode);
        tiao_ma_tv_Id.setTextColor(getResources().getColor(R.color.android_ciyao_text_color));
        qrcode_tv_id.setTextColor(getResources().getColor(R.color.android_fen_color));
        qrcode.setBackgroundResource(R.drawable.android_saoyisao_qr_pinkcohl_select);
        qrcode.setSelected(true);
        onecode.setSelected(false);
        viewfinderView.setVisibility(View.VISIBLE);
        qrcodeSetting();
        statusView.setText(R.string.scan_qrcode);
    }

    /**
     * 主要对相机进行初始化工作
     */
    @Override
    protected void onResume() {
        super.onResume();
        inactivityTimer.onActivity();
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        surfaceHolder = surfaceView.getHolder();
        setScanType();
        resetStatusView();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            // 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
            surfaceHolder.addCallback(this);
        }
        // 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
        beepManager.updatePrefs();
        // 恢复活动监控器
        inactivityTimer.onResume();
        //相册返回的路径  重新回到调用activity时会马上执行onActivityResult方法，然后才是onResume()方法   mHandler 报空
        if (TextUtils.isEmpty(photo_path)) {
            return;
        }
        Toast.makeText(CaptureActivity.this, photo_path, Toast.LENGTH_SHORT).show();
        Log.i(TAG + "onResume:111 ", "onResume: " + photo_path);
        startScan();
    }

//    public  void updateConnectState(){
//        ECDevice.ECConnectState connect = SDKCoreHelper.getConnectState();
//        if (connect == ECDevice.ECConnectState.CONNECTING) {
//            net_statu.setText("正在连接服务器");
//            if (net_statu.getVisibility()==View.GONE){
//                net_statu.setVisibility(View.VISIBLE);
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        onPause();
//                    }
//                },200);
//            }
//        } else if (connect == ECDevice.ECConnectState.CONNECT_FAILED) {
//            if (net_statu.getVisibility()==View.GONE){
//                net_statu.setVisibility(View.VISIBLE);
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        onPause();
//                    }
//                },200);
//            }
//        } else if (connect == ECDevice.ECConnectState.CONNECT_SUCCESS) {
//            if (net_statu.getVisibility()==View.VISIBLE){
//                net_statu.setVisibility(View.GONE);
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                     onResume();
//                    }
//                }, 200);
//            }
//        }
//    }

    /**
     * 展示状态视图和扫描窗口，隐藏结果视图
     */
    private void resetStatusView() {
//        resultView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.VISIBLE);
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the mHandler starts the preview, which can also throw a
            // RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, decodeFormats,
                        characterSet, cameraManager);
            }
            // decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 若摄像头被占用或者摄像头有问题则跳出提示对话框
     */
    private void displayFrameworkBugMessageAndExit() {
        ToastUtil.showMessage("若摄像头被占用或者摄像头有问题则跳出提示对话框");
//        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, R.string.msg_camera_framework_bug
//                , R.string.dialog_ok_button, new FinishListener(this));
//        buildAlert.setTitle(R.string.app_tip);
//        buildAlert.setCancelable(false);
//        buildAlert.show();
    }

    /**
     * 暂停活动监控器,关闭摄像头
     */
    @Override
    protected void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        // 暂停活动监控器
        inactivityTimer.onPause();
        // 关闭摄像头
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    /**
     * 停止活动监控器,保存最后选中的扫描类型
     */
    @Override
    protected void onDestroy() {
        // 停止活动监控器
        inactivityTimer.shutdown();
        saveScanTypeToSp();
        super.onDestroy();
    }

    /**
     * 保存退出进程前选中的二维码条形码的状态
     */
    private void saveScanTypeToSp() {
        SharedPreferences.Editor localEditor = this.mSharedPreferences.edit();
        localEditor.putString("currentState", CaptureActivity.currentState);
        localEditor.commit();
    }

    /**
     * 获取扫描结果
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        final boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {

            // Then not from history, so beep/vibrate and we have an image to
            // draw on
            beepManager.playBeepSoundAndVibrate();
//            drawResultPoints(barcode, scaleFactor, rawResult);
        }
//        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
//                DateFormat.SHORT);
        Map<ResultMetadataType, Object> metadata = rawResult
                .getResultMetadata();
        StringBuilder metadataText = new StringBuilder(20);
        if (metadata != null) {
            for (Map.Entry<ResultMetadataType, Object> entry : metadata
                    .entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }
            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
            }
        }
        resPondResult(rawResult);
        restartPreviewAfterDelay(500L);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("bitmap", barcode);
//        bundle.putString("barcodeFormat", rawResult.getBarcodeFormat()
//                .toString());
//        bundle.putString("decodeDate",
//                formatter.format(new Date(rawResult.getTimestamp())));
//        bundle.putCharSequence("metadataText", metadataText);

//        intent.putExtra(MyString.linkUrl, rawResult.getText());
//        Log.i(TAG, "getText: "+rawResult.getText()+"");
//        Log.i(TAG, "toString: "+rawResult.toString()+"");
//        Log.i(TAG, "getBarcodeFormat: "+rawResult.getBarcodeFormat()+"");
//        Log.i(TAG, "handleDecode: "+rawResult.+"");
//        Log.i(TAG, "handleDecode: "+rawResult.getText()+"");
//        Log.i(TAG, "handleDecode: "+rawResult.getText()+"");
//        Log.i(TAG, "handleDecode: " + rawResult.getText() + "");
        //???????????????????????????
//        dgdfgfdg
//        Toast.makeText(CaptureActivity.this,  rawResult.getText()+"jianyufeng", Toast.LENGTH_SHORT).show();
//        intent.setClass(CaptureActivity.this, ResultActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        CaptureActivity.this.finish();
//         handleDecodeInternally(rawResult, barcode);

    }

    private void resPondResult(Result rawResult) {
        if (TextUtils.isEmpty(rawResult.getText())) {
            Toast.makeText(CaptureActivity.this, "扫描的结果为空", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (DecodeThread.QR_CODE_FORMATS.contains(rawResult.getBarcodeFormat())) {
////            Log.i(TAG, "handleDecode: " + "二维码");
//            ErWeiMaSearchFriendBiz.erWeiMaSearchFriend(rawResult.getText(), this);
//            restartPreviewAfterDelay(500L);
//        } else if (DecodeThread.ONE_D_FORMATS.contains(rawResult.getBarcodeFormat())) {
//            String token = CCPAppManager.getContext().getSharedPreferences(MyString.USER_INFO, Context.MODE_PRIVATE).getString(MyString.USER_TOKEN, null);
//            String url = String.format(Url.TIAO_MA_URL, token, rawResult.getText());
////            Log.i(TAG, "handleDecode: " + "条码");
//            startActivity(new Intent(this, WebAboutActivity.class).putExtra(MyString.linkUrl, url));
//        }
    }

    //相册扫描成功返回
    public void success(Result rawResult) {
        photo_path = null;
//        dismissPostingDialog();
        resPondResult(rawResult);
    }

    //	相册扫描失败返回
    public void fail(String msg) {
        photo_path = null;
//        dismissPostingDialog();
        Toast.makeText(CaptureActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 在扫描图片结果中绘制绿色的点
     *
     * @param barcode
     * @param scaleFactor
     * @param rawResult
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor,
                                  Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.android_fen_color));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4
                    && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
                    .getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(),
                                scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    /**
     * 在扫描图片结果中绘制绿色的线
     *
     * @param canvas
     * @param paint
     * @param a
     * @param b
     * @param scaleFactor
     */
    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
                                 ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
                    scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
        }
    }

//    /**
//     * 显示扫描结果
//     *
//     * @param rawResult
//     * @param barcode
//     */
////    @SuppressWarnings("unused")
//    private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
//        statusView.setVisibility(View.GONE);
//        viewfinderView.setVisibility(View.GONE);
//        resultView.setVisibility(View.VISIBLE);

//        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
//        if (barcode == null) {
//            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(
//                    getResources(), R.drawable.ic_launcher));
//        } else {
//            barcodeImageView.setImageBitmap(barcode);
//        }

//        TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
//        formatTextView.setText(rawResult.getBarcodeFormat().toString());
//
//        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
//                DateFormat.SHORT);
//        TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
//        timeTextView
//                .setText(formatter.format(new Date(rawResult.getTimestamp())));
//
//        TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
//        View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
//        metaTextView.setVisibility(View.GONE);
//        metaTextViewLabel.setVisibility(View.GONE);
//        Map<ResultMetadataType, Object> metadata = rawResult
//                .getResultMetadata();
//        if (metadata != null) {
//            StringBuilder metadataText = new StringBuilder(20);
//            for (Map.Entry<ResultMetadataType, Object> entry : metadata
//                    .entrySet()) {
//                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
//                    metadataText.append(entry.getValue()).append('\n');
//                }
//            }
//            if (metadataText.length() > 0) {
//                metadataText.setLength(metadataText.length() - 1);
//                metaTextView.setText(metadataText);
//                metaTextView.setVisibility(View.VISIBLE);
//                metaTextViewLabel.setVisibility(View.VISIBLE);
//            }
//        }
//
//        TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);

    // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
//        contentsTextView.setText(rawResult.getText());
//        TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
//        supplementTextView.setText("");
//        supplementTextView.setOnClickListener(null);
//    }

    /**
     * 点击响应条形码扫描
     */
    private View.OnClickListener onecodeImageListener = new View.OnClickListener() {
        public void onClick(View paramAnonymousView) {
            tiao_ma_tv_Id.setTextColor(getResources().getColor(R.color.android_fen_color));
            qrcode_tv_id.setTextColor(getResources().getColor(R.color.android_ciyao_text_color));
            qrcode.setBackgroundResource(R.drawable.android_saoyisao_qr_pinkcodehl);
            onecode.setBackgroundResource(R.drawable.android_saoyisao_barcode_select);
            qrcode.setSelected(false);
            onecode.setSelected(true);
            statusView.setText(R.string.scan_onecode);
            viewfinderView.setVisibility(View.VISIBLE);
            currentState = "onecode";
            onecodeSetting();

        }
    };

    private void onecodeSetting() {
        decodeFormats = new Vector<BarcodeFormat>(7);
        decodeFormats.clear();
        decodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
//		scanTextView.setText(R.string.scan_one);
        if (null != mHandler) {
            mHandler.setDecodeFormats(decodeFormats);
        }

        viewfinderView.refreshDrawableState();
        //修改扫描匡的大小
        cameraManager.setManualFramingRect(DensityUtil.dip2px(280), DensityUtil.dip2px(150));
        viewfinderView.refreshDrawableState();
    }

    /**
     * 点击响应二维码扫描
     */
    private View.OnClickListener qrcodeImageListener = new View.OnClickListener() {
        public void onClick(View paramAnonymousView) {
            tiao_ma_tv_Id.setTextColor(getResources().getColor(R.color.android_ciyao_text_color));
            qrcode_tv_id.setTextColor(getResources().getColor(R.color.android_fen_color));
            onecode.setBackgroundResource(R.drawable.android_saoyisao_barcode);
            qrcode.setBackgroundResource(R.drawable.android_saoyisao_qr_pinkcohl_select);
            qrcode.setSelected(true);
            onecode.setSelected(false);
            statusView.setText(R.string.scan_qrcode);
            viewfinderView.setVisibility(View.VISIBLE);
            currentState = "qrcode";
            qrcodeSetting();

        }
    };

    private void qrcodeSetting() {
        decodeFormats = new Vector<BarcodeFormat>(2);
        decodeFormats.clear();
        decodeFormats.add(BarcodeFormat.QR_CODE);
        decodeFormats.add(BarcodeFormat.DATA_MATRIX);
//		scanTextView.setText(R.string.scan_qr);
        if (null != mHandler) {
            mHandler.setDecodeFormats(decodeFormats);
        }

        viewfinderView.refreshDrawableState();
        cameraManager.setManualFramingRect(DensityUtil.dip2px(250), DensityUtil.dip2px(250));
        viewfinderView.refreshDrawableState();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    /**
     * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (delayMS < 2000) {
            delayMS = 2000;
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK: // 拦截返回键
//                restartPreviewAfterDelay(0L);
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
             /*返回按钮点击事件*/
            case R.id.left:
                finish();
                break;
            case R.id.right_text:
                //打开手机中的相册
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//                // 如果朋友们要限制上传到服务器的图片类型时可以直接写如：image/jpeg 、 image/png等的类型
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    //获取选中图片的路径
                    if (data.getData() == null) {
                        Toast.makeText(CaptureActivity.this, "选取失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
//                        photo_path = DemoUtils.resolvePhotoFromIntent(this, data, FileAccessor.IMESSAGE_IMAGE);
//                        photo_path = getAbsoluteImagePath(data.getData());
                    } catch (Exception e) {
                        Toast.makeText(CaptureActivity.this, "找不到指定路径", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
        }
    }
    /**
     * 扫描二维码图片的方法
     * @param path
     * @return
     */
    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
//    public Result scanningImage(String path) {
//        try {
//
//
//            if (TextUtils.isEmpty(path)) {
//                Toast.makeText(CaptureActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
//                return null;
//            }
//            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
////            hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
////            hints.pu
//            Vector decodeFormats = new Vector<BarcodeFormat>();
//            decodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
//            decodeFormats.addAll(DecodeThread.QR_CODE_FORMATS);
//            decodeFormats.addAll(DecodeThread.DATA_MATRIX_FORMATS);
//            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true; // 先获取原大小
//            Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
//            options.inJustDecodeBounds = false; // 获取新的大小
//            int sampleSize = (int) (options.outHeight / (float) 200);
//            if (sampleSize <= 0)
//                sampleSize = 1;
//            options.inSampleSize = sampleSize;
//            scanBitmap = BitmapFactory.decodeFile(path, options);
//            com.fule.employee.util.zxing.RGBLuminanceSource source = new com.fule.employee.util.zxing.RGBLuminanceSource(scanBitmap);
//            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
////            QRCodeReader reader = new QRCodeReader();
//            MultiFormatReader reader = new MultiFormatReader();
//            try {
//                return reader.decode(bitmap1, hints);
//
//            } catch (NotFoundException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            Toast.makeText(CaptureActivity.this, "图片解析异常", Toast.LENGTH_SHORT).show();
//        }
//        return null;
//    }
    private Bitmap scanBitmap;

    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;

        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {

            e.printStackTrace();

        } catch (ChecksumException e) {

            e.printStackTrace();

        } catch (FormatException e) {

            e.printStackTrace();

        }
        return null;

    }

    //    //将uri转换成 path；
//    protected String getAbsoluteImagePath(Uri uri) throws Exception {
//        // can post image
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri,
//                proj,                 // Which columns to return
//                null,       // WHERE clause; which rows to return (all rows)
//                null,       // WHERE clause selection arguments (none)
//                null);                 // Order-by clause (ascending by name)
//
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        return cursor.getString(column_index);
//    }
    public void startScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = scanningImage(photo_path);
                if (scanBitmap != null) {
                    scanBitmap.recycle();
                }
                if (result != null) {
                    Message m = mHandler.obtainMessage();
                    m.what = 20;
                    m.obj = result;
                    if (mHandler != null) {
                        mHandler.sendMessage(m);
                    }
                } else {
                    Message m = mHandler.obtainMessage();
                    m.what = 21;
                    m.obj = "扫描无结果";
                    if (mHandler != null) {
                        mHandler.sendMessage(m);
                    }
                }

            }
        }).start();
    }

//    //关闭Dialog
//    private static void dismissPostingDialog() {
//        if (mPostingdialog == null || !mPostingdialog.isShowing()) {
//            return;
//        }
//        mPostingdialog.dismiss();
//        mPostingdialog = null;
//    }
//
//    //显示提交Dialog
//    public static ECProgressDialog mPostingdialog;

}
