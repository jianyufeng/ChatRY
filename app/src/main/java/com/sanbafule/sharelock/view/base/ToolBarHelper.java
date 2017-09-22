package com.sanbafule.sharelock.view.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.global.MyString;


/**
 * Created by Administrator on 2016/10/31.
 */
public class ToolBarHelper {


    /*上下文，创建view的时候需要用到*/
    private Context mContext;
    /*base view*/
    private FrameLayout mContentView;
    /*用户定义的view*/
    private View mUserView;
    /*toolbar*/
    private Toolbar mToolBar;
    /*视图构造器*/
    private LayoutInflater mInflater;
    //toolbar 的view
    private ImageView left;
    private TextView title;
    private TextView subtitle;
    private TextView right_text;
    private ImageView right_img;
    private View.OnClickListener mOnClickListener;
    // 遮罩的布局
    private View coverLayout;


    private   boolean isShowToolbar;
    /*
    * 两个属性
    * 1、toolbar是否悬浮在窗口之上
    * 2、toolbar的高度获取
    *
    */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId, boolean isShowToolbar) {
        this.mContext = context;
         this.isShowToolbar=isShowToolbar;
        mInflater = LayoutInflater.from(mContext);
        /*初始化整个内容*/
        initContentView();
        //初始化遮罩布局
        initCoverLayout();
         /*初始化用户定义的布局*/
        initUserView(layoutId);
         /*初始化toolbar*/

        initToolBar();

    }

    private void initCoverLayout() {
        coverLayout = mInflater.inflate(R.layout.base_cover_layout, null);

    }

    private void initContentView() {
    /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView=new FrameLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);

    }

    private void initToolBar() {
    /*通过inflater获取toolbar的布局文件*/
        if (!isShowToolbar) return;
        View toolbar = mInflater.inflate(R.layout.toolbar, mContentView);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.id_tool_bar);
        left = (ImageView) toolbar.findViewById(R.id.left);
        title = (TextView) toolbar.findViewById(R.id.title);
        subtitle = (TextView) toolbar.findViewById(R.id.subtitle);
        right_text = (TextView) toolbar.findViewById(R.id.right_text);
        right_img = (ImageView) toolbar.findViewById(R.id.right_img);
    }

    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

      if(isShowToolbar){
          TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
             /*获取主题中定义的悬浮标志*/
          boolean overly = typedArray.getBoolean(0, false);
        /*获取主题中定义的toolbar的高度*/
          int toolBarSize = (int) typedArray.getDimension(1, (int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
          typedArray.recycle(); /*如果是悬浮状态，则不需要设置间距*/
          params.topMargin = overly ? 0 : toolBarSize;
      }
        mContentView.addView(mUserView, params);
        mContentView.addView(coverLayout);


    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    public View getCoverLayout() {
        return coverLayout;
    }


    public void setLeft(int resId) {
        if (resId > 0) {
            left.setVisibility(View.VISIBLE);
            left.setImageResource(resId);
            if (mOnClickListener != null) {
                left.setOnClickListener(mOnClickListener);
            }
        } else {
            left.setVisibility(View.GONE);
        }
    }


    public void setRightText(int resId) {
        if (resId > 0) {
            right_text.setVisibility(View.VISIBLE);
            right_text.setText(resId);
            if (mOnClickListener != null) {
                right_text.setOnClickListener(mOnClickListener);
            }
        } else {
            right_text.setVisibility(View.GONE);
        }
    }

    public void setRightText(String resId) {
        if (MyString.hasData(resId)) {
            right_text.setVisibility(View.VISIBLE);
            right_text.setText(resId);
            if (mOnClickListener != null) {
                right_text.setOnClickListener(mOnClickListener);
            }
        } else {
            right_text.setVisibility(View.GONE);
        }
    }

    public void setRightImg(int resId) {
        if (resId > 0) {
            right_img.setVisibility(View.VISIBLE);
            right_img.setImageResource(resId);
            if (mOnClickListener != null) {
                right_img.setOnClickListener(mOnClickListener);
            }
        } else {
            right_img.setVisibility(View.GONE);
        }
    }

    public void setTitle(int resId) {
        if (resId > 0) {
            title.setVisibility(View.VISIBLE);
            title.setText(resId);
        } else {
            title.setVisibility(View.GONE);
        }
    }

    public void setTitle(String resId) {
        if (MyString.hasData(resId)) {
            title.setVisibility(View.VISIBLE);
            title.setText(resId);
        } else {
            title.setVisibility(View.GONE);
        }
    }

    public void setSubTitle(int resId) {
        if (resId > 0) {
            subtitle.setVisibility(View.VISIBLE);
            subtitle.setText(resId);
        } else {
            subtitle.setVisibility(View.GONE);
        }
    }

    public void setSubTitle(String resId) {
        if (MyString.hasData(resId)) {
            subtitle.setVisibility(View.VISIBLE);
            subtitle.setText(resId);
        } else {
            subtitle.setVisibility(View.GONE);
        }
    }

    public void setToolBarStates(int left, int title, int subtitle, int right_text, int right_img, View.OnClickListener listener) {
        this.mOnClickListener = listener;
        setLeft(left);
        setRightText(right_text);
        setRightImg(right_img);
        setTitle(title);
        setSubTitle(subtitle);

    }

    public void setToolBarStates(int left, String title, String subtitle, String right_text, int right_img, View.OnClickListener listener) {
        this.mOnClickListener = listener;
        setLeft(left);
        setRightText(right_text);
        setRightImg(right_img);
        setTitle(title);
        setSubTitle(subtitle);

    }


    public void setToolBarStates(int left, int title, int right_text, int right_img, View.OnClickListener listener) {
        this.mOnClickListener = listener;
        setLeft(left);
        setRightText(right_text);
        setRightImg(right_img);
        setTitle(title);
    }

    public void setToolBarStates(int left, String title, String right_text, int right_img, View.OnClickListener listener) {
        this.mOnClickListener = listener;
        setLeft(left);
        setRightText(right_text);
        setRightImg(right_img);
        setTitle(title);
    }

    public TextView getRightTextView() {
        return right_text;
    }
}
