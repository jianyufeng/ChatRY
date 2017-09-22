package com.sanbafule.sharelock.chatting.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.DownImgBiz;
import com.sanbafule.sharelock.chatting.modle.ViewImageInfo;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;
import java.util.List;

public class ImageGralleryPagerActivity extends BaseActivity implements View.OnClickListener {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static boolean isFireMsg = false;
    /**
     *
     */
    private boolean mFullscreen = true;
    private ViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private List<ViewImageInfo> urls;

    @Override
    public int getLayoutId() {
        return R.layout.image_grallery_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageGalleryFragment.i = 0;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "1 / 1", "", -1, this);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        urls = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_URLS);

        if (urls == null || urls.isEmpty()) {
            finish();
            return;
        }
        if (pagerPosition > urls.size()) {
            pagerPosition = 0;
        }

        getToolBarHelper().setTitle(pagerPosition + "/" + (urls != null ? urls.size() : 0));
        mPager = (ViewPager) findViewById(R.id.pager);
        final ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        // 点击图片下载
        findViewById(R.id.imagebrower_iv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    ViewImageInfo viewImageInfo = urls.get(mPager.getCurrentItem());
                    if (viewImageInfo != null && MyString.hasData(viewImageInfo.getPicurl())) {
                        DownImgBiz biz = new DownImgBiz();
                        biz.downLoadImage(ImageGralleryPagerActivity.this, viewImageInfo.getPicurl(), viewImageInfo.getUserName());
                    }

                }
            }
        });


        // 更新下标
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                // 更改下面的页面
                initIndicatorIndex(arg0);
                // 更改标题
                setTitleNumber(arg0);

            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
        initIndicatorIndex(pagerPosition);
        setTitleFooterVisible(true);
    }

    private void setTitleNumber(int arg0) {
        CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new RelativeSizeSpan(1.5F), 0, text.toString().indexOf("/"), SpannableString.SPAN_PRIORITY);
        getToolBarHelper().setTitle(spannableString.toString());

    }

    private void initIndicatorIndex(int arg0) {
        CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new RelativeSizeSpan(1.5F), 0, text.toString().indexOf("/"), SpannableString.SPAN_PRIORITY);
        indicator.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        mHandlerCallbck.sendEmptyMessageDelayed(1, 350L);
        switch (v.getId()) {
//            case R.id.left_back:
//                hideSoftKeyboard();
//                finish();
//                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (urls != null) {
            urls.clear();
            urls = null;
        }
        if (mHandlerCallbck != null) {
            mHandlerCallbck.removeCallbacksAndMessages(null);
        }
        mPager = null;
        isFireMsg = false;
        ImageGalleryFragment.i = 0;
        System.gc();

    }

    private final Handler mHandlerCallbck = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            mFullscreen = !mFullscreen;
            setTitleFooterVisible(mFullscreen);
        }

    };


    /**
     * @param request
     */
    private void requestStatusbars(boolean request) {
        if (request) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return;
        }
        LogUtil.d(LogUtil.getLogUtilsTag(getClass()), "request custom title");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Full screen, hidden actionBar
     *
     * @param visible
     */
    void setTitleFooterVisible(boolean visible) {
        if (visible) {
            requestStatusbars(false);
//            showTitleView();
            return;
        }

        requestStatusbars(true);
//        hideTitleView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public List<ViewImageInfo> fileList;

        public ImagePagerAdapter(FragmentManager fm, List<ViewImageInfo> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            ViewImageInfo url = fileList.get(position);
            return ImageGalleryFragment.newInstance(url);
        }

    }

}