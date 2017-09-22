package com.sanbafule.sharelock.UI.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/13 14:33
 * cd : 三八妇乐
 * 描述：
 */
public class NewsFragment extends BaseFragment {


    @Bind(R.id.news_list)
    RecyclerView newsList;
    @Bind(R.id.news_fresh_layout)
    SwipeRefreshLayout newsFreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
