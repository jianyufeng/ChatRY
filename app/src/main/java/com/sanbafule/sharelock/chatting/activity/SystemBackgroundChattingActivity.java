package com.sanbafule.sharelock.chatting.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.chatting.modle.SystemBackgroundBean;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.sanbafule.sharelock.chatting.activity.ImageGalleryFragment.i;

public class SystemBackgroundChattingActivity extends BaseActivity {


    @Bind(R.id.system_bg_list)
    ExpandRecyclerView systemBgList;

    private RecycleViewBaseAdapter adapter;

    private List<SystemBackgroundBean> list;

    private SystemBackgroundBean bean;
    private static final int CONNT = 3;
    private int width;

    @Override
    public int getLayoutId() {
        return R.layout.activity_system_background_chatting_ativity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "选择背景图", null, -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
        int displayWidth = DensityUtil.getDisplayWidth(SystemBackgroundChattingActivity.this);
        int i = DensityUtil.dip2px(20L);
        width = (displayWidth - i) / CONNT;
        //设置布局管理器
        GridLayoutManager manager = new GridLayoutManager(this, CONNT);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        systemBgList.setLayoutManager(manager);
//        final int a = DensityUtil.getDisplayWidth(this) - DensityUtil.dip2px(240F);
        systemBgList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.android_width_10);
//                outRect.left = a / 4;
                int i = parent.getChildLayoutPosition(view);
                if (i % CONNT == CONNT - 1) {
                    outRect.right = (int) getResources().getDimension(R.dimen.android_width_10);
                } else if (i % CONNT == 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.android_width_10);
                } else {
                    outRect.left = (int) getResources().getDimension(R.dimen.android_width_10);
                    outRect.right = (int) getResources().getDimension(R.dimen.android_width_10);
                }
            }
        });
        //设置适配器
        adapter = new RecycleViewBaseAdapter<SystemBackgroundBean, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_system_background);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                bean = list.get(position);
                if (bean == null) return;
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = layoutParams.width = width;
                holder.itemView.setLayoutParams(layoutParams);
                if (bean.isDownloaded()) {
                    holder.getView(R.id.selected).setVisibility(View.VISIBLE);
                    if (bean.isSelected()) {
                        holder.getView(R.id.selected).setBackgroundResource(R.drawable.cb_bg_checked);
                    } else {
                        holder.getView(R.id.selected).setBackgroundResource(R.drawable.cb_bg_unchecked);
                    }

                    holder.getView(R.id.download).setVisibility(View.GONE);

                } else {
                    holder.getView(R.id.download).setVisibility(View.VISIBLE);
                }
                holder.setImageResource(R.id.pic, bean.getUrl());
            }
        };
        systemBgList.setAdapter(adapter);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            bean = new SystemBackgroundBean();
            if (i < 2) {
                bean.setDownloaded(true);
            }
            if (i == 1) {
                bean.setSelected(true);
            }
            bean.setUrl("R.drawable.ic_launcher");
            list.add(bean);
        }
        adapter.fillList(list);
    }
}
