package com.sanbafule.sharelock.view.BottomTab;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.view.RoundNumber;

import java.util.ArrayList;
import java.util.List;




@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainNavigateTabBar extends LinearLayout implements View.OnClickListener {

    private static final String KEY_CURRENT_TAG = "MainNavigateTabBar";
   // ViewHolder 的集合
    private List<ViewHolder> mViewHolderList;
    // Tab点击的接口
    private OnTabSelectedListener mTabSelectListener;
    // Activity对象
    private FragmentActivity mFragmentActivity;
    // 当前的tag
    private String mCurrentTag;
    // 要返回的tag
    private String mRestoreTag;
    /*主内容显示区域View的id*/
    private int mMainContentLayoutId;
    /*选中的Tab文字颜色*/
    private ColorStateList mSelectedTextColor;
    /*正常的Tab文字颜色*/
    private ColorStateList mNormalTextColor;
    /*Tab文字的尺寸*/
    private float mTabTextSize;
    /*默认选中的tab index*/
    private int mDefaultSelectedTab = 0;
    // 当前选中的Tab
    private int mCurrentSelectedTab;

    private ViewHolder holder;
    public MainNavigateTabBar(Context context) {
        this(context, null);
    }

    public MainNavigateTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MainNavigateTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /***
         * 获取自定义属性
         */
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MainNavigateTabBar, 0, 0);
        //文字的颜色
        ColorStateList tabTextColor = typedArray.getColorStateList(R.styleable.MainNavigateTabBar_navigateTabTextColor);
       //文字选中的颜色
        ColorStateList selectedTabTextColor = typedArray.getColorStateList(R.styleable.MainNavigateTabBar_navigateTabSelectedTextColor);
        //文字的大小
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.MainNavigateTabBar_navigateTabTextSize, 0);
        //对应的layoutId
        mMainContentLayoutId = typedArray.getResourceId(R.styleable.MainNavigateTabBar_containerId, 0);
        //没选中的文字的颜色
        mNormalTextColor = (tabTextColor != null ? tabTextColor : context.getResources().getColorStateList(R.color.tab_text_normal));


        if (selectedTabTextColor != null) {
            mSelectedTextColor = selectedTabTextColor;
        } else {
            // 检查主题
            ThemeUtils.checkAppCompatTheme(context);
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            mSelectedTextColor = context.getResources().getColorStateList(typedValue.resourceId);
        }

        mViewHolderList = new ArrayList<>();


    }

    /**
     * 添加Tab
     * @param frameLayoutClass
     * @param tabParam  TabView
     */
    public void addTab(Class frameLayoutClass, TabParam tabParam) {
        int defaultLayout = R.layout.comui_tab_view;
        if (tabParam.tabViewResId > 0) {
            defaultLayout = tabParam.tabViewResId;
        }
        if (TextUtils.isEmpty(tabParam.title)) {
            tabParam.title = getContext().getString(tabParam.titleStringRes);
        }
        // 设置viewHolder 属性

        View view = LayoutInflater.from(getContext()).inflate(defaultLayout, null);
        view.setFocusable(true);

        holder = new ViewHolder();

        holder.tabIndex = mViewHolderList.size();

        holder.fragmentClass = frameLayoutClass;
        holder.tag = tabParam.title;
        holder.pageParam = tabParam;
        // 图片
        holder.tabIcon = (ImageView) view.findViewById(R.id.tab_icon);
        // 文字
        holder.tabTitle = ((TextView) view.findViewById(R.id.tab_title));
        // 带文字的小红点
        holder.NubDot= (RoundNumber) view.findViewById(R.id.myunreadtext);
//        // 不带文字的小红点
//        holder.RedDot= (ImageView) view.findViewById(R.id.unread_dot_img);

        if (TextUtils.isEmpty(tabParam.title)) {
            holder.tabTitle.setVisibility(View.INVISIBLE);
        } else {
            holder.tabTitle.setText(tabParam.title);
        }

        if (mTabTextSize != 0) {
            holder.tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
        }
        if (mNormalTextColor != null) {
            holder.tabTitle.setTextColor(mNormalTextColor);
        }

        if (tabParam.backgroundColor > 0) {
            view.setBackgroundResource(tabParam.backgroundColor);
        }

        if (tabParam.iconResId > 0) {
            holder.tabIcon.setImageResource(tabParam.iconResId);
        } else {
            holder.tabIcon.setVisibility(View.INVISIBLE);
        }

        if (tabParam.iconResId > 0 && tabParam.iconSelectedResId > 0) {
            view.setTag(holder);
            view.setOnClickListener(this);
            mViewHolderList.add(holder);
        }

        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));

    }



    /**
     * 更新未读消息的接口
     * Update the interface number of unread
     *
     * @param unreadCount
     */
    public final void updateMainTabUnread(int unreadCount, int index) {

        if (unreadCount > 0) {
            if (unreadCount > 99) {
                this.setUnreadTips("...", index);
                return;
            }
            this.setUnreadTips(String.valueOf(unreadCount), index);
            return;
        }
        this.setUnreadTips(null, index);
    }


    public final void setUnreadTips(final String count, final int index) {


        if (TextUtils.isEmpty(count)) {
            mViewHolderList.get(index).NubDot.setVisibility(View.GONE);
            return;
        }


        mViewHolderList.get(index).NubDot.setVisibility(View.VISIBLE);
        mViewHolderList.get(index).NubDot.post(new Runnable() {

            @Override
            public void run() {
                mViewHolderList.get(index).NubDot.setMessage(count);
                // 重新绘制
                mViewHolderList.get(index).NubDot.invalidate();
            }
        });
    }
    public  final  RoundNumber getNubDot(int index){
        if(index>mViewHolderList.size()){
            return null;
        }
       return mViewHolderList.get(index).NubDot;
    }


    /**
     * 在View和Window绑定时就会调用这个函数，
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mMainContentLayoutId == 0) {
            throw new RuntimeException("mFrameLayoutId Cannot be 0");
        }
        if (mViewHolderList.size() == 0) {
            throw new RuntimeException("mViewHolderList.size Cannot be 0, Please call addTab()");
        }
        if (!(getContext() instanceof FragmentActivity)) {
            throw new RuntimeException("parent activity must is extends FragmentActivity");
        }
        mFragmentActivity = (FragmentActivity) getContext();

        ViewHolder defaultHolder = null;

        hideAllFragment();
        if (!TextUtils.isEmpty(mRestoreTag)) {
            for (ViewHolder holder : mViewHolderList) {
                if (TextUtils.equals(mRestoreTag, holder.tag)) {
                    defaultHolder = holder;
                    mRestoreTag = null;
                    break;
                }
            }
        } else {
            defaultHolder = mViewHolderList.get(mDefaultSelectedTab);
        }

        showFragment(defaultHolder);
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object != null && object instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) v.getTag();
            showFragment(holder);
            if (mTabSelectListener != null) {
                mTabSelectListener.onTabSelected(holder);
            }
        }
    }

    /**
     * 显示 holder 对应的 fragment
     *
     * @param holder
     */

    private void showFragment(ViewHolder holder) {
        FragmentTransaction transaction;
        Log.e("FragmentTransaction", "dffds" + mFragmentActivity);
        transaction=mFragmentActivity.getSupportFragmentManager().beginTransaction();

        if (isFragmentShown(transaction, holder.tag)) {
            return;
        }

       setCurrSelectedTabByTag(holder.tag);
        mCurrentSelectedTab = holder.tabIndex;
        Fragment fragment = mFragmentActivity.getSupportFragmentManager().findFragmentByTag(holder.tag);
        if (fragment == null) {
            fragment = getFragmentInstance(holder.tag);
            transaction.add(mMainContentLayoutId, fragment, holder.tag);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();

    }


    public void showFragment(String tag,int tabIndex) {
        FragmentTransaction transaction;
        transaction=mFragmentActivity.getSupportFragmentManager().beginTransaction();

        if (isFragmentShown(transaction, tag)) {
            return;
        }

        setCurrSelectedTabByTag(tag);
        mCurrentSelectedTab = tabIndex;
        Fragment fragment = mFragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = getFragmentInstance(tag);
            transaction.add(mMainContentLayoutId, fragment,tag);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();

    }
    /**
     * 判断Fragment是否在显示
     * @param transaction
     * @param newTag
     * @return
     */
    private boolean isFragmentShown(FragmentTransaction transaction, String newTag) {
        if (TextUtils.equals(newTag, mCurrentTag)) {
            return true;
        }

        if (TextUtils.isEmpty(mCurrentTag)) {
            return false;
        }

        Fragment fragment = mFragmentActivity.getSupportFragmentManager().findFragmentByTag(mCurrentTag);
        if (fragment != null && !fragment.isHidden()) {
            transaction.hide(fragment);
        }

        return false;
    }

    /**
     * 设置当前选中tab的图片和文字颜色
     * @param tag 选中的tag
     */
    private void setCurrSelectedTabByTag(String tag) {
        if (TextUtils.equals(mCurrentTag, tag)) {
            return;
        }
        for (ViewHolder holder : mViewHolderList) {
            if (TextUtils.equals(mCurrentTag, holder.tag)) {
                holder.tabIcon.setImageResource(holder.pageParam.iconResId);
                holder.tabTitle.setTextColor(mNormalTextColor);
            } else if (TextUtils.equals(tag, holder.tag)) {
                holder.tabIcon.setImageResource(holder.pageParam.iconSelectedResId);
                holder.tabTitle.setTextColor(mSelectedTextColor);
            }
        }
        mCurrentTag = tag;
    }


    /**
     * 获取Fragment的实体
     *
     * @param tag
     * @return
     */
    private Fragment getFragmentInstance(String tag) {
        Fragment fragment = null;
        for (ViewHolder holder : mViewHolderList) {
            if (TextUtils.equals(tag, holder.tag)) {
                try {
                    fragment = (Fragment) Class.forName(holder.fragmentClass.getName()).newInstance();
                } catch (InstantiationException e) {

                    Log.e("MyTag", "错误是" + e + "InstantiationException");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.e("MyTag", "错误是" + e + "IllegalAccessException");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return fragment;
    }

    /**
     * 隐藏所有的Fragmnet
     */
    public void hideAllFragment() {
        if (mViewHolderList == null || mViewHolderList.size() == 0) {
            return;
        }
        FragmentTransaction transaction = mFragmentActivity.getSupportFragmentManager().beginTransaction();

        for (ViewHolder holder : mViewHolderList) {
            Fragment fragment = mFragmentActivity.getSupportFragmentManager().findFragmentByTag(holder.tag);
            if (fragment != null && !fragment.isHidden()) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    public void setSelectedTabTextColor(ColorStateList selectedTextColor) {
        mSelectedTextColor = selectedTextColor;
    }

    public void setSelectedTabTextColor(int color) {
        mSelectedTextColor = ColorStateList.valueOf(color);
    }

    public void setTabTextColor(ColorStateList color) {
        mNormalTextColor = color;
    }

    public void setTabTextColor(int color) {
        mNormalTextColor = ColorStateList.valueOf(color);
    }

    public void setFrameLayoutId(int frameLayoutId) {
        mMainContentLayoutId = frameLayoutId;
    }

    /**
     *  Fragment 的复用
     * @param savedInstanceState
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRestoreTag = savedInstanceState.getString(KEY_CURRENT_TAG);
        }
    }

    /**
     * Fragment的保存
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURRENT_TAG, mCurrentTag);
    }

    public static class ViewHolder  {
        // 标签
        public String tag;
        //TabParam 参数
        public TabParam pageParam;
        // 图片
        public ImageView tabIcon;
        // 文字
        public TextView tabTitle;
        // Fragment
        public Class fragmentClass;
        // Tab的个数
        public int tabIndex;
        // 带文字的小红点
        public RoundNumber NubDot;
//        // 不带文字的小红点
//        public ImageView RedDot;

    }

    /**
     * TabView
     */
    public static class TabParam {
        //背景颜色
        public int backgroundColor = android.R.color.white;
        // 文选中的图片的Id
        public int iconResId;
        // 选中的图片的Id
        public int iconSelectedResId;
        // 文字的id
        public int titleStringRes;
        // layout——id
        public int tabViewResId;
        //文字
        public String title;

        public TabParam(int iconResId, int iconSelectedResId, String title) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
        }

        public TabParam(int iconResId, int iconSelectedResId, int titleStringRes) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, int titleStringRes) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, String title) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
        }
    }


    public interface OnTabSelectedListener {
        void onTabSelected(ViewHolder holder);
    }

    public void setTabSelectListener(OnTabSelectedListener tabSelectListener) {
        mTabSelectListener = tabSelectListener;
    }

    /**
     * 设置默认选中的Tab
     * @param index
     */
    public void setDefaultSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            mDefaultSelectedTab = index;
        }
    }

    /**
     * 设置当前选中的Tab
     * @param index
     */
    public void setCurrentSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            ViewHolder holder = mViewHolderList.get(index);
            showFragment(holder);
        }
    }

    /**
     * 获得当前选中的Tab
     * @return
     */
    public int getCurrentSelectedTab(){
        return mCurrentSelectedTab;
    }

    public Fragment getFragment(String holder){
        return mFragmentActivity.getSupportFragmentManager().findFragmentByTag(holder);

    }

}
