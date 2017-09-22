package com.sanbafule.sharelock.UI.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.contact.ContactInfoActivity;
import com.sanbafule.sharelock.UI.contact.NewFriendInfoActivity;
import com.sanbafule.sharelock.UI.group.ApplyJoinGroupActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class BaseSearchActivity extends BaseActivity {

    @Bind(R.id.search_input)  //输入框
            ShareLockClearEditText searchInput;
    @Bind(R.id.cancel_search)  //取消
            TextView cancelSearch;
    @Bind(R.id.search_RecyclerView)  //搜索结果的容器
            ExpandRecyclerView searchRecyclerView;
    @Bind(R.id.search_toolbar)
    Toolbar toolbar;

    private BaseSearchActivity mContext;
    //输入的内容
    private String content;


    private int type;  //类型
    private RecycleViewBaseAdapter adapter;
    private TextView searchTv;
    private TextView emptyView;


    //item的点击事件
    private RecyclerViewClick.Click onItemClick = new RecyclerViewClick.Click() {
        @Override
        public void onItemClick(View view, int position) {
            switch (type) {
                case 0:  //搜索出来的朋友点击事件
                    ContactInfo contactInfo = (ContactInfo) adapter.getItem(position);
                    if (contactInfo == null) {
                        return;
                    }
                    String u_username = contactInfo.getU_username();
                    if (TextUtils.isEmpty(u_username)) {
                        return;
                    }
                    if (u_username.equals(ShareLockManager.getInstance().getUserName())) {
                        ToastUtil.showMessage("自己不能和自己聊天");
                    }
                    if (ContactSqlManager.hasThisFriend(u_username)) {
                        //有跳到好友详情
                        startActivity(new Intent(mContext, ContactInfoActivity.class)
                                .putExtra(SString.NAME, u_username)
                                .putExtra(SString.CONTACTINFO, contactInfo));
                        finish();
                    } else {
                        //跳到添加好友的界面
                        startActivity(new Intent(mContext, NewFriendInfoActivity.class)
                                .putExtra(SString.CONTACTINFO, contactInfo));
                        finish();
                    }
                    break;
                case 1: //搜索出来的群组点击事件
                    GroupInfo groupInfo = (GroupInfo) adapter.getItem(position);
                    if (groupInfo == null) {
                        return;
                    }
                    String g_id = groupInfo.getG_id();
                    if (TextUtils.isEmpty(g_id)) {
                        return;
                    }
                    //是自己的群组
                    if (GroupSqlManager.isExitGroup(g_id)) {
                        startActivity(new Intent(mContext, ApplyJoinGroupActivity.class)
                                .putExtra(SString.GROUP_PRE, groupInfo)
                                .putExtra(SString.TYPE, false));
                    } else {
                        //申请添加群组
                        startActivity(new Intent(mContext, ApplyJoinGroupActivity.class)
                                .putExtra(SString.GROUP_PRE, groupInfo)
                                .putExtra(SString.TYPE, true));
                    }
                    break;
                case 3:
                    ContactInfo info = (ContactInfo) adapter.getItem(position);
                    if (info == null) {
                        return;
                    }
                    String username = info.getU_username();
                    if (TextUtils.isEmpty(username)) {
                        return;
                    }
                    if (username.equals(ShareLockManager.getInstance().getUserName())) {
                        ToastUtil.showMessage("自己不能和自己聊天");
                    }
                    startActivity(new Intent(mContext, ContactInfoActivity.class)
                            .putExtra(SString.NAME, username)
                            .putExtra(SString.CONTACTINFO, info));
                    finish();
                    break;
                case 4 :
                    GroupInfo item = (GroupInfo) adapter.getItem(position);
                    if (item==null)return;
                    //是自己的群组
                    if (GroupSqlManager.isExitGroup(item.getG_id())) {
                        startActivity(new Intent(mContext, ApplyJoinGroupActivity.class)
                                .putExtra(SString.GROUP_PRE, item)
                                .putExtra(SString.TYPE, false));
                    } else {
                        //申请添加群组
                        startActivity(new Intent(mContext, ApplyJoinGroupActivity.class)
                                .putExtra(SString.GROUP_PRE, item)
                                .putExtra(SString.TYPE, true));
                    }
                default:
                    break;
            }

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        type = intent.getIntExtra(SString.TYPE, -1);
        String inputHint = intent.getStringExtra(SString.SEARCH_HINT);//输入框的提示文本
        String inputText = intent.getStringExtra(SString.SEARCH_TEXT);//输入框的默认显示文本
        //设置提示
        if (!TextUtils.isEmpty(inputHint)) {
            searchInput.setHint(inputHint);
        }
        if (!TextUtils.isEmpty(inputText)) {
            searchInput.setText(inputText);

        }
        initSearchBefore();
    }


    //根据类型做些搜索前的初始化工作
    private void initSearchBefore() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (adapter.getItemCount() > 0) {
                    adapter.cleanAllDatas();
                }
                emptyView.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                searchTv.setText(content);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(manager);
        //设置分割线
        searchRecyclerView.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));


        //需要添加的代码   创建适配器
        switch (type) {
            case 0:
                initSearchFriendBefore();
                break;
            case 1:
                initSearchGroupBefore();
                break;
            // 搜索本地联系人
            case 3:
                initSearchLocalFriendBefore();
                break;
            case 4:
                initSearchLocalGroupBefore();
                break;
            default:
                break;
        }
        //设置适配器
        searchRecyclerView.setAdapter(adapter);
        //设置点击事件
        adapter.setItemClickAndLongClick(onItemClick);
        //添加底部视图
        View item_foot = LayoutInflater.from(this).inflate(R.layout.item_search_foot, searchRecyclerView, false);
        item_foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputText()) {
                    searchByType();
                }
            }
        });
        searchTv = (TextView) item_foot.findViewById(R.id.content);
        emptyView = (TextView) item_foot.findViewById(R.id.empty_view);
        adapter.addFooterView(item_foot);
    }

    private void initSearchLocalGroupBefore() {
        adapter = new RecycleViewBaseAdapter<GroupInfo, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_group_list);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                GroupInfo item = getItem(position);
                if (item == null) {
                    return;
                }
                String g_name = item.getG_name();
                if (!TextUtils.isEmpty(g_name)) {
                    holder.setText(R.id.group_name, g_name);
                }
                holder.setImageResource(R.id.group_icon, ShareLockManager.getImgUrl(item.getG_header()));
            }
        };
    }



    private void initSearchLocalFriendBefore() {
        //创建列表的适配器
        adapter = new RecycleViewBaseAdapter<ContactInfo, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_friend);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, final int position) {
                ContactInfo item = getItem(position);

                if (item == null) {
                    return;
                }
                //名称

                holder.setText(R.id.name, item.getName());
                holder.setImageResource(R.id.icon, ShareLockManager.getImgUrl(item.getU_header()));
            }
        };
        //设置适配器

    }

    //搜索朋友前的初始化工作
    private void initSearchFriendBefore() {
        //创建列表的适配器
        adapter = new RecycleViewBaseAdapter<ContactInfo, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_friend);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, final int position) {
                ContactInfo item = getItem(position);
                if (item == null) {
                    return;
                }
                //名称
                holder.setText(R.id.name, item.getName());
                holder.setImageResource(R.id.icon, ShareLockManager.getImgUrl(item.getU_header()));
            }
        };
        //设置适配器

    }

    //搜索群组前的初始化工作
    private void initSearchGroupBefore() {
        //创建群组列表的适配器
        adapter = new RecycleViewBaseAdapter<GroupInfo, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_group_list);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, final int position) {
                GroupInfo item = getItem(position);
                if (item == null) {
                    return;
                }
                String g_id = item.getG_id();

                GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(g_id);
                if (groupInfo == null) {
                    return;
                }
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
    }

    private boolean checkInputText() {
        //检查输入的内容是否符要求
        content = searchInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showMessage(getString(R.string.search_empty), Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    //根据传入的类型进行不同的搜索
    private void searchByType() {
        showDiglog();
        switch (type) {
            case 0:
                searchFriend();
                break;
            case 1:
                searchGroup();
                break;
            case 3:
                searchLocalFriend();
            case 4:
                searchLocalGroup();
            default:
        }
    }

    private void searchLocalGroup() {
        List<GroupInfo> groupInfos = GroupInfoSqlManager.searchGroup(content);
        if (groupInfos != null && groupInfos.size() > 0) {
            //显示
            adapter.fillList(groupInfos);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        closeDialog();
    }

    /**
     *搜索联系人
     */
    private void searchFriend() {

        GroupService.getMemberDetails(content, new GroupService.Callback<ContactInfo, Void>() {
            @Override
            public void success(ContactInfo c, Void aVoid) {
                closeDialog();
                if (c != null && !TextUtils.isEmpty(c.getU_username())) {
                    ArrayList<ContactInfo> contactInfos = new ArrayList<>();
                    contactInfos.add(c);
                    adapter.fillList(contactInfos);

                }

            }

            @Override
            public void fail() {
                closeDialog();
                emptyView.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 搜索本地好友
     */
    private void searchLocalFriend() {
        if (ContactSqlManager.hasThisFriend(content)) {
            //有跳到好友详情
            ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(content);
            if (contactInfo == null) {
                GroupService.getMemberDetails(content, new GroupService.Callback<ContactInfo, Void>() {
                    @Override
                    public void success(ContactInfo c, Void aVoid) {
                        closeDialog();
                        if (c != null && !TextUtils.isEmpty(c.getU_username())) {
                            ArrayList<ContactInfo> contactInfos = new ArrayList<>();
                            contactInfos.add(c);
                            adapter.fillList(contactInfos);

                        }

                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                closeDialog();

                ArrayList<ContactInfo> contactInfos = new ArrayList<>();
                contactInfos.add(contactInfo);
                adapter.fillList(contactInfos);
            }
        } else {
            closeDialog();
            emptyView.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 搜索群组
     */
    private void searchGroup() {
        GroupService.searchGroup(content, new GroupService.Callback<ArrayList<GroupInfo>, Void>() {
            @Override
            public void success(ArrayList<GroupInfo> s, Void aVoid) {
                closeDialog();
                if (s != null && s.size() > 0) {
                    adapter.fillList(s);
                }
            }

            @Override
            public void fail() {
                closeDialog();
                emptyView.setVisibility(View.VISIBLE);
            }
        });

    }


    @OnClick({R.id.cancel_search})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_search:
                hideSoftKeyboard();
                finish();
                break;


        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_base_search;
    }


    @Override
    public boolean isNeedToolBar() {
        return false;
    }
}
