
package com.sanbafule.sharelock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;


/**
 * 设置选项
 * 简玉锋 2016-11-28
 */
public class DatailSettingItem extends RelativeLayout {


    /**
     * Item内容区域
     */  //左边的内容区域
    private RelativeLayout mContent;
    /**
     * 标题View
     */  //左边图像
    private ImageView icon;
    /**
     * 等级图标
     */
    private ImageView level_icon;
    /**
     * 等级图标
     */
    private ImageView manager_icon;
    /**
     * 用户的昵称
     */
    private TextView nickName;

    /**
     * 等级文本
     */
    private TextView level_tv;
    /**
     * 管理文本
     */
    private TextView manager_tv;

    /**
     * 标记的昵称
     */
    private TextView label;
    /**
     * 账号
     */
    private TextView account;

    /**
     * 右边的文本内容
     */
    private TextView mTextRight;


    /**
     * 分割线
     */
    private View mDividerView;


    /**
     * 第三行的显示的文本名称
     */

    private TextView thirdText;

    /**
     * 是否显示分割线
     */
    private boolean mShowDivider;

    //下页标志
    private View nextPage;
    //显示下页标示
    private boolean mShowNextPage;
    public View qrcode;//二维码


    /**
     * @param context
     * @param attrs
     */
    public DatailSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_group_member_details_setting, this, true);

        mContent = (RelativeLayout) findViewById(R.id.content);//左边的内容区域
        icon = (ImageView) findViewById(R.id.icon);//左边图像

        nickName = (TextView) findViewById(R.id.nick_name); // 用户的昵称

        level_icon = (ImageView) findViewById(R.id.level_icon);//等级图标

        manager_icon = (ImageView) findViewById(R.id.manager_icon);//管理图标

        level_tv = (TextView) findViewById(R.id.level_tv); // 等级文本

        manager_tv = (TextView) findViewById(R.id.manager_tv); // 等级文本

        label = (TextView) findViewById(R.id.label); // 标记昵称文本

        account = (TextView) findViewById(R.id.account); // 账号

        mTextRight = (TextView) findViewById(R.id.right_tv); // 右边的文本

        mDividerView = findViewById(R.id.item_bottom_divider);   //底部的分割线
        nextPage = findViewById(R.id.next_page);   // 下页标志

        qrcode = findViewById(R.id.qrcode);   // 二维码
        thirdText = (TextView) findViewById(R.id.third_text);


        //通过自定义的属性来从布局中获取对应的设置信息  进行初始化
        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.setting_icon);

        //设置是否label昵称
        setLabelVisibility(localTypedArray.getBoolean(R.styleable.setting_icon_item_showLabel, false));
        //设置label昵称的内容
        setLabelString(localTypedArray.getString(R.styleable.setting_icon_item_LabelString));

        //设置是否显示等级  默认不显示
        setLevelVisibility(localTypedArray.getBoolean(R.styleable.setting_icon_item_showLevel, false));
        //设置等级内容
        setLevelString(localTypedArray.getString(R.styleable.setting_icon_item_LevelString));
        //设置是否显示管理  默认不显示
        setManagerVisibility(localTypedArray.getBoolean(R.styleable.setting_icon_item_showManager, false));
        //设置管理内容
        setManagerString(localTypedArray.getString(R.styleable.setting_icon_item_ManagerString));
        //设置底部的分割线是否显示  默认显示
        setShowDivider(localTypedArray.getBoolean(R.styleable.setting_icon_item_showDividerFlag, true));

        //设置显示下页标示  默认不显示
        setShowNextPage(localTypedArray.getBoolean(R.styleable.setting_icon_item_showNextPageFlag, false));
        // 设置右边文本的内容
        setRightText(localTypedArray.getString(R.styleable.setting_icon_item_rightTextString));
        //设置二维码
        setQrcodeVisibility(localTypedArray.getBoolean(R.styleable.setting_icon_item_showQrcode, false));
        // 设置第三行的文本内容
        setThridText(localTypedArray.getString(R.styleable.setting_icon_item_thirdString));

        //回收对应的TypedArray
        localTypedArray.recycle();
    }

    private void setThridText(String text) {
        if (text == null) {
            thirdText.setText("");
            return;
        }
        thirdText.setText(text);
    }


    //获取图像控件
    public ImageView getIcon() {
        return icon;
    }

    /**
     * 设置昵称
     *
     * @param text
     */
    public void setNickName(String text) {
        if (text == null) {
            nickName.setText("");
            return;
        }
        nickName.setText(text);
    }

    //设置等级是否显示
    public void setLevelVisibility(boolean visibility) {
        if (level_icon != null && level_tv != null) {
            level_icon.setVisibility(visibility ? View.VISIBLE : View.GONE);
            level_tv.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    //设置管理是否显示
    public void setManagerVisibility(boolean visibility) {
        if (manager_tv != null && manager_icon != null) {
            manager_tv.setVisibility(visibility ? View.VISIBLE : View.GONE);
            manager_icon.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    //设置等级内容
    public void setLevelString(String text) {
        if (text == null) {
            level_tv.setText("");
            return;
        }

        level_tv.setText(text);
    }

    //设置管理内容
    public void setManagerString(String text) {
        if (text == null) {
            manager_tv.setText("");
            return;
        }
        manager_tv.setText(text);
    }

    //设置label 从昵称
    public void setLabelString(String text) {
        if (text == null) {
            label.setText("");
            return;
        }
        label.setText(text);
    }

    //设置账号
    public void setAccountString(String text) {
        if (text == null) {
            account.setText("");
            return;
        }
        account.setText(text);
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
     * 设置右边文本内容
     *
     * @param text
     */
    public void setRightText(String text) {
        if (text == null) {
            mTextRight.setText("");
            return;
        }
        mTextRight.setText(text);
    }

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
     * 设置二维码显示状态
     *
     * @param visibility
     */
    private void setQrcodeVisibility(boolean visibility) {
        if (qrcode != null) {
            qrcode.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置label昵称
     *
     * @param visibility
     */
    private void setLabelVisibility(boolean visibility) {
        if (label != null) {
            label.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }
}
