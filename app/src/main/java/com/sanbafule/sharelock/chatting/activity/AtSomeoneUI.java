package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupMember;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.util.ArrayList;

/**
 * 作者:Created by 简玉锋 on 2017/2/8 14:12
 * 邮箱: jianyufeng@38.hn
 */
public class AtSomeoneUI extends BaseActivity implements View.OnClickListener {
    //群组成员
    private ExpandRecyclerView membersContainer;
    //空view
    private View emptyView;
    //适配器
    private RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder> adapter;
    //群组成员
    private ArrayList<GroupMember> groupMembers;
    //群组id
    private String groupId;
    //设置点击事件
    private RecyclerViewClick.Click click = new RecyclerViewClick.Click() {
        @Override
        public void onItemClick(View view, int position) {
            GroupMember item = adapter.getItem(position);
            if (item == null) {
                return;
            }
            String u_username = item.getU_username();
            if (!TextUtils.isEmpty(u_username)) {
                finshSetResult(u_username);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.room_at_someone), null, -1, this);
        initView();
        asynData();
        logicControlView();
    }

    protected void asynData() {
        showCoverLayout();
        //异步数据
        GroupService.getGroupAndMemberDetails(groupId, new GroupService.Callback<GroupInfo, ArrayList<GroupMember>>() {
            @Override
            public void success(GroupInfo s, ArrayList<GroupMember> m) {
                if (s == null || m == null) {
                    return;
                }
                closeCoverLayout();
//                groupInfo = s;  //获取新的群组详情 刷新群组详情数据
                groupMembers = m;  // 获取群组成员  刷新群组成员
                refreshData();

            }

            @Override
            public void fail() {
                ToastUtil.showMessage(getString(R.string.group_details_get_err));
                closeCoverLayout();
            }
        });


    }

    private void refreshData() {
        if (groupMembers == null) return;
        adapter.appendList(groupMembers);

    }


    private void logicControlView() {
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        membersContainer.setLayoutManager(manager);
        //设置空View
        membersContainer.setEmptyView(emptyView);
        //设置分割线
        membersContainer.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));    //创建群组列表的适配器
        //创建群组列表的适配器
        //设置适配器
        adapter = new RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                //创建布局
                return new RecycleViewBaseHolder(parent, R.layout.item_group_list);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                //绑定数据
                GroupMember item = getItem(position);
                if (item == null) {
                    return;
                }
                //群名片
                String name = item.getGur_card();
                if (TextUtils.isEmpty(name)) {
                    //账号
                    name = item.getU_username();
                }
                holder.setText(R.id.group_name, name);
                //成员图像
                String url = item.getU_header();
                if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                    url = Url.CORE_IP + url;
                }
                holder.setImageResource(R.id.group_icon, url);
            }
        };

        //设置适配器
        membersContainer.setAdapter(adapter);
        //添加头部搜索视图
        View headView = LayoutInflater.from(this).inflate(R.layout.activity_at_head_layout, membersContainer, false);
        //搜索
        View search = headView.findViewById(R.id.searchLayout);
        search.setOnClickListener(this);
        //@ all
        View atAll = headView.findViewById(R.id.at_all);
        atAll.setOnClickListener(this);
        adapter.addHeaderView(headView);

        adapter.setItemClickAndLongClick(click);
    }

    private void initView() {
        groupId = getIntent().getStringExtra(SString.GROUP_ID);
        if (TextUtils.isEmpty(groupId)) {
            finish();
            return;
        }
        membersContainer = (ExpandRecyclerView) findViewById(R.id.recycle_view_id);
        emptyView = findViewById(R.id.id_empty_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                finish();
                break;
            case R.id.searchLayout:
                ToastUtil.showMessage("search");
                break;
            case R.id.at_all:
                finshSetResult(getString(R.string.at_all));
                break;
        }
    }

    private void finshSetResult(String s) {
        Intent intent = new Intent();
        intent.putExtra(SString.GROUP_MEMBER_ACCOUNT, s);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_list;
    }
}
