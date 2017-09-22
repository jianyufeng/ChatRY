package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.base.BaseSearchActivity;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.util.ArrayList;


/**
 * Created by 简玉锋 on 2016/11/19.
 * 群组列表   显示所加入的群组
 */

public class GroupListActivity extends BaseActivity implements View.OnClickListener {
    //群组列表的容器
    private ExpandRecyclerView groupListContainer;
    //空view
    private View emptyView;
    //适配器
    private RecycleViewBaseAdapter<Group, RecycleViewBaseHolder> adapter;
    //群组数据
    private ArrayList<Group> groups;
    //群组个数
    private TextView groupNumber;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.group_list_title), getString(R.string.group_list_create_group), -1, this);


        //注册群组列表插入完成的广播  注册解散群组  注册退出群组
        registerReceiver(new String[]{ReceiveAction.ACTION_DISMISS_GROUP, ReceiveAction.ACTION_QUIT_GROUP});
        initData();
        initView();
        asynData();
        logicControlView();
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新数据
        initData();
        refreshData();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //群组列表界面需要刷新的控件可以在此进行添加
    private void refreshData() {
        if (adapter == null) {
            return;
        }
        //添加数据并更新
        adapter.fillList(groups);
        //添加底部群组数量视图
        groupNumber.setText(getString(R.string.group_number, String.valueOf(adapter.getItemCount() - adapter.getExtraViewCount())));
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_group_list;
    }


    public void initData() {
        //获取群组数据
        groups = GroupSqlManager.getGroups();
        if (groups == null) {
            groups = new ArrayList<>();
        }
    }

    public void initView() {
        groupListContainer = (ExpandRecyclerView) findViewById(R.id.recycle_view_id);
        emptyView = findViewById(R.id.id_empty_view);
    }

    public void asynData() {
        //异步数据
        // 1 主线程回调
        // 2 异步插入到数据库中
        GroupService.getGroupList(new GroupService.Callback<ArrayList<Group>, Void>() {
            @Override
            public void success(ArrayList<Group> s, Void v) {
                if (s == null) {
                    return;
                }
                groups = s;  //保存数据
                refreshData(); //刷新数据
            }

            @Override
            public void fail() {

            }
        });

    }


    public void logicControlView() {
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        groupListContainer.setLayoutManager(manager);
        //设置空View
        groupListContainer.setEmptyView(emptyView);
        //设置分割线
        groupListContainer.addItemDecoration(new RecycleViewLinearDivider(GroupListActivity.this, LinearLayoutManager.VERTICAL));    //创建群组列表的适配器
        //创建群组列表的适配器
        adapter = new RecycleViewBaseAdapter<Group, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_group_list);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                Group item = getItem(position);
                if (item == null) {
                    return;
                }
                String g_id = item.getG_id();

                GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(g_id);
                if (groupInfo == null) {
                    GroupInfoBiz.getGroupInfo(g_id, new GetGroupInfoListener() {
                        @Override
                        public void getGroupInfo(GroupInfo groupInfo) {
                            setGroupInfo(holder, groupInfo);
                        }
                    });

                    return;
                }
                setGroupInfo(holder, groupInfo);
            }

            private void setGroupInfo(RecycleViewBaseHolder holder, GroupInfo groupInfo) {
                String g_name = groupInfo.getG_name();
                if (!TextUtils.isEmpty(g_name)) {
                    holder.setText(R.id.group_name, g_name);
                }
                String url = groupInfo.getG_header();
                if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                    url = Url.CORE_IP + url;
                }
                holder.setImageResource(R.id.group_icon, url);
            }
        };

        //设置适配器
        groupListContainer.setAdapter(adapter);
        //添加头部搜索视图
        View headView = LayoutInflater.from(this).inflate(R.layout.base_search_layout, groupListContainer, false);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BaseSearchActivity.class)
                        .putExtra(SString.TYPE, 4)
                        .putExtra(SString.SEARCH_HINT, getString(R.string.search_group_hint)));
            }
        });
        adapter.addHeaderView(headView);
        View footView = LayoutInflater.from(this).inflate(R.layout.contact_foot_item, groupListContainer, false);
        groupNumber = (TextView) footView.findViewById(R.id.number);
        adapter.addFooterView(footView);
        adapter.setItemClickAndLongClick(click);
//        groupListContainer.addOnItemTouchListener(new RecycleViewItemClickListener(groupListContainer, new RecycleViewItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                //跳转到搜索界面   点击的是头部视图
//                if (position == 0) {
////                    Intent intent = new Intent(GroupListActivity.this, BaseSearchActivity.class);
////                    startActivity(intent);
//                    ToastUtil.showMessage("跳转到搜索界面");
//                    return;
//                }
//
//                //获取群组  跳转到群组聊天界面
//                Group item = adapter.getItem(position);
//                if (item == null) { //点击的可能是 头部或底部视图
//                    return;
//                }
//                ToastUtil.showMessage(item.getG_id());
//                startActivity(new Intent(context, GroupDetailsActivity.class).putExtra(SString.GROUP_ID, item.getG_id()));
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        }));
    }

    //设置点击事件
    private RecyclerViewClick.Click click = new RecyclerViewClick.Click() {
        @Override
        public void onItemClick(View view, int position) {
            Group item = adapter.getItem(position);
            if (item == null) {
                return;
            }
            ShareLockManager.startGroupChattingActivity(context, item.getG_id(), null, true);
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:       //跳转到创建群组界面
                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
                GroupListActivity.this.startActivity(intent);
                break;

        }
    }

    // 广播接收器  接受群组列表插入完成的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_QUIT_GROUP.equals(intent.getAction())) {
            /**1  接受到群组退出的广播
             2  从新拉取数据
             3刷新数据
             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId)) {
                initData();
                refreshData();
            }
        } else if (ReceiveAction.ACTION_DISMISS_GROUP.equals(intent.getAction())) {
            /**1  接受到群组解散的广播
             2  从新拉取数据
             3刷新数据
             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId)) {
                initData();
                refreshData();
            }
        }
    }
}
