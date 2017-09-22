package com.sanbafule.sharelock.UI.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.util.ArrayList;

import butterknife.Bind;


/**
 * Created by Administrator on 2016/5/4.
 */
public class InformationFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.navID)
    LinearLayout navID;
    @Bind(R.id.slidingID)
    View slidingID;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList();


    /**
     * 当前显示的TabView Fragment
     */
    private int mCurrentItemPosition = 0;

    public int getInformationFgCt() {
        return mCurrentItemPosition;
    }

    public static InformationFragment mInstance;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInstance = this;
        initView();
    }

    private void initView() {
        ViewGroup.LayoutParams params = slidingID.getLayoutParams();
        int childCount = navID.getChildCount();
        params.width = DensityUtil.getDisplayWidth(getActivity()) / childCount;
        for (int i = 0; i < childCount; i++) {
            TextView title = (TextView) navID.getChildAt(i);
            title.setText(SString.INFORMATION_TITLE[i]);
            final int finalI = i;
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtil.cancel();
                    viewPager.setCurrentItem(finalI);
                }
            });
        }
        viewPager.setOffscreenPageLimit(4);
//        fragments.add(new NewsFragment());
//        fragments.add(new ProjectBigEventFragment());
//        fragments.add(new ProjectAdvanceFragment());
//        fragments.add(new MovieFragment());
        new InformationViewPagerAdapter(getFragmentManager(), viewPager);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_information;
    }


    @Override
    public void onClick(View view) {

    }





    private class InformationViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        public InformationViewPagerAdapter(FragmentManager fm, ViewPager viewPager) {
            super(fm);
            viewPager.setAdapter(this);
            viewPager.addOnPageChangeListener(this);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            slidingID.setTranslationX((slidingID.getMeasuredWidth()) * (positionOffset + position));
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentItemPosition = position;
            for (int i = 0; i < navID.getChildCount(); i++) {
                TextView tv = (TextView) navID.getChildAt(i);
                if (position == i) {
                    tv.setTextColor(getResources().getColor(R.color.android_fen_color));
                } else {
                    tv.setTextColor(Color.BLACK);
                }
            }
            setRightHeadImg();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void setRightHeadImg() {
        switch (mCurrentItemPosition) {
//            case 0:
//                    MainActivity.mLauncherUI.setSearchType(4,"");
//                MainActivity.mLauncherUI.setRightImgVisable(false, true,false);
//                MainActivity.mLauncherUI.setType(4,"搜索新闻资讯");
//
//                break;
//            case 1:
//                MainActivity.mLauncherUI.setRightImgVisable(false,false,false);
//                break;
//            case 2:
//                MainActivity.mLauncherUI.setSearchType(5,"");
//                MainActivity.mLauncherUI.setType(5, "搜索工程推进会");
//                MainActivity.mLauncherUI.setRightImgVisable(false, true,false);
//                break;
//            case 3:
//                MainActivity.mLauncherUI.setRightImgVisable(false,false,false);
//                break;

        }
    }
}
