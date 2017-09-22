package com.sanbafule.sharelock.UI.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.SDKVersionUtils;
import com.sanbafule.sharelock.view.base.ToolBarHelper;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/28.
 */

@ActivityTransition(0)
public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    protected ToolBarHelper mToolBarHelper;
    protected Toolbar toolbar;
    /**
     * 初始化广播接收器
     */
    private InternalReceiver internalReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        abstracrRegist();

    }

    @Override
    public void setContentView(int layoutResID) {
        mToolBarHelper = new ToolBarHelper(this, layoutResID, isNeedToolBar());
        setContentView(mToolBarHelper.getContentView());
        /*把 toolbar 设置到Activity 中*/
        if (isNeedToolBar()) {
            toolbar = mToolBarHelper.getToolBar();
            setSupportActionBar(toolbar); /*自定义的一些操作*/
            onCreateCustomToolBar(toolbar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(internalReceiver);
        System.gc();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(WindowAnimation.activityCloseEnterAnimation, WindowAnimation.activityCloseExitAnimation);
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setContentInsetsRelative(0, 0);
    }


    // Internal calss.
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            handleReceiver(context, intent);
        }
    }

    /**
     * 如果子界面需要拦截处理注册的广播
     * 需要实现该方法
     *
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理\
        if (intent == null) {
            return;
        }
        if (ReceiveAction.ACTION_KICK_OFF.equals(intent.getAction())) {
            //弹出对话框
            finish();
        }
    }


    /********************************************************/
    /**
     * 注册广播
     *
     * @param actionArray
     */

    protected final void registerReceiver(String[] actionArray) {

        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(ReceiveAction.ACTION_KICK_OFF);
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }

    private void abstracrRegist() {
        registerReceiver(new String[]{ReceiveAction.ACTION_KICK_OFF});

    }

    /**
     * hide inputMethod
     * <p>
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = this.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * 软键盘的翻转
     */
    public void toggleSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = this.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected ToolBarHelper getToolBarHelper() {
        return mToolBarHelper;
    }

    protected void showCoverLayout() {
        getToolBarHelper().getCoverLayout().setVisibility(View.VISIBLE);
    }

    protected void closeCoverLayout() {
        getToolBarHelper().getCoverLayout().setVisibility(View.GONE);
    }

    public abstract int getLayoutId();

    private WindowAnimation mAnimation = new WindowAnimation();

    private void onStartActivityAction(Intent intent) {
        /***
         *给activity跳转加上动画效果
         */
//        if(intent == null) {
        super.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//            return ;
//        }
//        String className = null;
//        ComponentName component = intent.getComponent();
//        if(component != null) {
//            className = component.getClassName();
//            if (!(className.startsWith(component.getPackageName()))) {
//                className = component.getPackageName() + component.getClassName();
//            }
//        } else {
//            return ;
//        }
//        if((0x2 & MethodInvoke.getTransitionValue(className)) != 0) {
//            super.overridePendingTransition(mAnimation.openEnter, mAnimation.openExit);
//            return ;
//        }
//
//        if((0x4 & MethodInvoke.getTransitionValue(className)) != 0) {
//            MethodInvoke.startTransitionNotChange(this);
//            return ;
//        }
//        MethodInvoke.startTransitionPopin(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        super.onSaveInstanceState(outState);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void startActivities(Intent[] intents) {
        super.startActivities(intents);
        onStartActivityAction(null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivities(Intent[] intents, Bundle bundle) {
        super.startActivities(intents, bundle);
        onStartActivityAction(null);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        onStartActivityAction(intent);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void startActivity(Intent intent, Bundle bundle) {
        super.startActivity(intent, bundle);
        onStartActivityAction(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        onStartActivityAction(intent);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        onStartActivityAction(intent);
    }

    protected MaterialDialog mPostingdialog;

    protected void showDiglog() {
        mPostingdialog = DialogUtils.showIndeterminateProgressDialog(this, false);
    }

    protected void closeDialog() {
        if (mPostingdialog == null || !mPostingdialog.isShowing()) {
            return;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }


    public static class WindowAnimation {
        public static int activityOpenEnterAnimation;
        public static int activityOpenExitAnimation;
        public static int activityCloseEnterAnimation;
        public static int activityCloseExitAnimation;

        public int openEnter = activityOpenEnterAnimation;
        public int openExit = activityOpenExitAnimation;
        public int closeEnter = activityCloseEnterAnimation;
        public int closeExit = activityCloseExitAnimation;

        static {
            if (!SDKVersionUtils.isSmallerVersion(19)) {
                activityOpenEnterAnimation = R.anim.slide_right_in;
                activityOpenExitAnimation = R.anim.slide_left_out;
                activityCloseEnterAnimation = R.anim.slide_left_in;
                activityCloseExitAnimation = R.anim.slide_right_out;
            } else {
                activityOpenEnterAnimation = R.anim.pop_in;
                activityOpenExitAnimation = R.anim.anim_not_change;
                activityCloseEnterAnimation = R.anim.anim_not_change;
                activityCloseExitAnimation = R.anim.pop_out;
            }
        }
    }


    public boolean isNeedToolBar() {
        return true;
    }

}
