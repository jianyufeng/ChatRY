package com.sanbafule.sharelock.UI.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.fragment.BaseFragment;
import com.sanbafule.sharelock.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/12 16:20
 * cd : 三八妇乐
 * 描述：
 */
public class TestFragment extends Fragment {

    private View view;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.i("Fuck You");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_user_info, container, false);
        refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.refresh_and_load_layout_id);
        listView= (RecyclerView) view.findViewById(R.id.service_list);
        return view;
    }
}
