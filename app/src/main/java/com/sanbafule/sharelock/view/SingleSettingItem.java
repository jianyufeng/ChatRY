
package com.sanbafule.sharelock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;


/**
 * 设置选项
 * 简玉锋 2016-11-28
 */
public class SingleSettingItem extends RelativeLayout {

    /**
     * 切换按钮
     */
    public static final int ACCESSORY_TYPE_CHECKBOX = 2; //显示开关

    /**
     * Item内容区域
     */  //左边的内容区域
    private LinearLayout mContent;
    /**
     * 标题View
     */  //左边文本
    private TextView mTitle;
    /**
     * 概要View
     */
//    private TextView mSummary;
    /**
     * 切换View
     */
    private SwitchCompat mCheckedTextView;
    private TextView mTextRight;
    /**
     * 右边的文本内容
     */
    private String rightText;


    /**
     * 分割线
     */
    private View mDividerView;
    /**
     * 附加类型
     */
    private int mAccessoryType;
    /**
     * 是否显示分割线
     */
    private boolean mShowDivider;
    /**
     * 标题
     */
    private String mTitleText;
    /**
     * 概要文字
     */
//    private String mSummaryText;
    private int[] mInsetDrawableRect = {0, 0, 0, 0};

    //下页标志
    private View nextPage;
    //显示下页标示
    private boolean mShowNextPage;

    //右边图片控件
    private ImageView rightImage;
    //是否显示右边的图片控件
    private boolean showRightIamge;

    /**
     * @param context
     * @param attrs
     */
    public SingleSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_details_setting_item, this, true);

        mContent = (LinearLayout) findViewById(R.id.content);//左边的内容区域
        mTitle = (TextView) findViewById(android.R.id.title);//左边文本
//        mSummary = (TextView) findViewById(android.R.id.summary); //概要
        mTextRight = (TextView) findViewById(R.id.text_tv_one); // 右边的文本
        mCheckedTextView = (SwitchCompat) findViewById(R.id.accessory_checked); //切换View 开关
        mDividerView = findViewById(R.id.item_bottom_divider);   //底部的分割线
        nextPage = findViewById(R.id.next_page);   // //下页标志
        rightImage = (ImageView) findViewById(R.id.right_image);
        //通过自定义的属性来从布局中获取对应的设置信息  进行初始化
        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.setting_info);
        // 设置 左边文本的内容
        setTitleText(localTypedArray.getString(R.styleable.setting_info_item_titleText));
        // 设置由边文本的内容
        setRightText(localTypedArray.getString(R.styleable.setting_info_item_rightText));
        //设置概要文本内容
//        setDetailText(localTypedArray.getString(R.styleable.setting_info_item_detailText));
        //设置切换的开关可见属性
        setSwitchVisible(localTypedArray.getBoolean(R.styleable.setting_info_item_switch_visible, false));
        //设置底部的分割线是否显示  默认显示
        setShowDivider(localTypedArray.getBoolean(R.styleable.setting_info_item_showDivider, true));
        //设置显示下页标示  默认不显示
        setShowNextPage(localTypedArray.getBoolean(R.styleable.setting_info_item_showNextPage, false));
        //设置显示右边的图片
        setShowRightImage(localTypedArray.getBoolean(R.styleable.setting_info_item_showRightImage, false));
        //回收对应的TypedArray
        setRightImageSize((int) localTypedArray.getDimension(R.styleable.setting_info_item_RightImage_size, (int) (getResources().getDimension(R.dimen.android_width_108))));
        localTypedArray.recycle();
    }

    private void setRightImageSize(int size) {
        ViewGroup.LayoutParams layoutParams = rightImage.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        rightImage.setLayoutParams(layoutParams);
    }

    /**
     * @param showDivider 设置显示底部分割线
     */
    private void setShowDivider(boolean showDivider) {
        mShowDivider = showDivider;
        View dividerView = mDividerView;
        dividerView.setVisibility(mShowDivider ? View.VISIBLE : View.GONE);
    }

    /**
     * @param showNextPage 设置显示下页标示
     */
    public void setShowNextPage(boolean showNextPage) {
        mShowNextPage = showNextPage;
        nextPage.setVisibility(mShowNextPage ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * @param showRightImage 设置显示右边的图片
     */
    public void setShowRightImage(boolean showRightImage) {
        showRightIamge = showRightImage;
        rightImage.setVisibility(showRightIamge ? View.VISIBLE : View.GONE);
    }

    //获取右边图片控件
    public ImageView getRightImage() {
        return rightImage;
    }

    /**
     * 设置标题信息  左边文本内容
     *
     * @param text
     */
    public void setTitleText(String text) {
        mTitleText = text;
        if (text == null) {
            mTitle.setText("");
            return;
        }
        mTitle.setText(mTitleText);
    }

    /**
     * 设置右边文本内容
     *
     * @param text
     */
    public void setRightText(String text) {
        rightText = text;
        if (text == null) {
            mTextRight.setText("");
            return;
        }
        mTextRight.setText(rightText);
    }

    //设置开关位置的文本内容
    public void setCheckText(boolean on, String text) {
        if (text != null) {
            if (on) {
                mCheckedTextView.setTextOn(text);
            } else {

                mCheckedTextView.setTextOff(text);
            }

        }
    }

    /**
     * 设置概要文本内容
     * <p>
     * //     * @param text
     */
//    public void setDetailText(String text) {
//        mSummaryText = text;
//        if (text == null) {
//            mSummary.setText("");
//            mSummary.setVisibility(View.GONE);
//            return;
//        }
//        mSummary.setText(mSummaryText);
//        mSummary.setVisibility(View.VISIBLE);
//    }
    @SuppressWarnings("deprecation")
//    private void setSettingBackground(int resid) {
//        int[] rect = new int[4];
//        rect[0] = getPaddingLeft();
//        rect[1] = getPaddingTop();
//        rect[2] = getPaddingRight();
//        rect[3] = getPaddingBottom();
//        if (isInsetDrawable()) {
//            setBackgroundDrawable(new InsetDrawable(getContext().getResources()
//                    .getDrawable(resid), mInsetDrawableRect[0],
//                    mInsetDrawableRect[1], mInsetDrawableRect[2],
//                    mInsetDrawableRect[3]));
//        } else {
//            setBackgroundResource(resid);
//        }
//        setPadding(rect[0], rect[1], rect[2], rect[3]);
//    }

    /**
     * 设置右边文本的内容
     *
     * @param visibility
     */
    public void setRightTextVisibility(boolean visibility) {
        if (mTextRight != null) {
            mTextRight.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * @return
     */
    private boolean isInsetDrawable() {
        for (int i = 0; i < mInsetDrawableRect.length; i++) {
            if (mInsetDrawableRect[i] <= 0) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 设置开关是否可见
     *
     * @param visible
     */
    private void setSwitchVisible(boolean visible) {
        if (mCheckedTextView != null) {
            mCheckedTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 返回切换按钮
     *
     * @return
     */
    public SwitchCompat getCheckedTextView() {
        return mCheckedTextView;
    }

    /**
     * 检测开关是否处于选中状态
     *
     * @return
     */
    public boolean isChecked() {

        return mCheckedTextView.isChecked();

    }

    /**
     * 设置开关的状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        mCheckedTextView.setChecked(checked);
    }

}
