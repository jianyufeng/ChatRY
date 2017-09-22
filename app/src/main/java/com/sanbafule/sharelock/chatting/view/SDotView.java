package com.sanbafule.sharelock.chatting.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.util.DensityUtil;

/**
 * Created by Administrator on 2016/10/9.
 */
public class SDotView extends LinearLayout {
    // 圆点的宽度
    private int viewWidth;
    // 每个view中的间距
    private int marginWidth;
    private int mIndicatorBackgroundResId = R.drawable.ic_launcher;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.unread_dot;
    private ViewPager pager;
    private int mLastPosition=-1;
  private Context mContext;

    public SDotView(Context context,int viewWidth) {
        super(context);
        this.mContext=context;
        this.viewWidth=viewWidth;
        this.marginWidth=viewWidth*2;
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
        setPadding(0, viewWidth / 2, 0, viewWidth / 2);

    }

    public void setViewPager(ViewPager pager){
        removeAllViews();
            this.pager=pager;
        if(pager!=null&pager.getAdapter()!=null){
            mLastPosition = -1;
            createIndicators();
            pager.setOnPageChangeListener(mInternalPageChangeListener);

            mInternalPageChangeListener.onPageSelected(pager.getCurrentItem());
        }

    }

    /***
     * 创建指示器的关键代码
     */
    private void createIndicators() {
        removeAllViews();
        int count = pager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        int currentItem = pager.getCurrentItem();

        for (int i = 0; i < count; i++) {
            if (currentItem == i) {
                addIndicator(i,count,mIndicatorBackgroundResId);
            } else {
                addIndicator(i,count,mIndicatorUnselectedBackgroundResId);
            }
        }
    }

    /**
     * 添加子view
     *
     * @param backgroundDrawableId
     */
    private void addIndicator(int position,int count,@DrawableRes int backgroundDrawableId) {

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        addView(Indicator, viewWidth, viewWidth);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        int scWidth= DensityUtil.getScWidth(mContext);
        int fristLeftMargin=(scWidth-((viewWidth*count)+marginWidth*(count-1)))/2;
        if(position==0){
            lp.leftMargin=fristLeftMargin;
        }else {
            lp.leftMargin = marginWidth;
//            lp.rightMargin = marginWidth;
        }
        Indicator.setLayoutParams(lp);

    }

    /**
     * 逻辑代码
     * <p/>
     * 滑动效果对应的视图变化
     */
    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // 数据不合法是 return
            if (pager.getAdapter() == null || pager.getAdapter().getCount() <= 0) {
                return;
            }

            View currentIndicator;
            if (mLastPosition >= 0 && (currentIndicator = getChildAt(mLastPosition)) != null) {
                currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
            }
            /**
             * 当page选中设置当前对应的view的背景和动画
             */
            View selectedIndicator = getChildAt(position);
            if (selectedIndicator != null) {
                selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
            }
            mLastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /***
     * 观察者
     *
     * @return
     */
    public DataSetObserver getDataSetObserver() {
        return mInternalDataSetObserver;
    }

    /**
     * viewPager 中的数据发生改变是动态的创建 此指示器
     */
    private DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (pager == null) {
                return;
            }

            int newCount = pager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {  // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = pager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }

            createIndicators();
        }
    };

}
