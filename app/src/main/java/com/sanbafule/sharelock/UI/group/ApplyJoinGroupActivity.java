package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupMemberService;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupMember;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.SingleSettingItem;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 作者:Created by 简玉锋 on 2016/11/25 15:56
 * 邮箱: jianyufeng@38.hn
 * 群组申请加入界面
 */

public class ApplyJoinGroupActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;

    //群图像
    private SingleSettingItem groupIconItem;
    //群组创建时间
    private SingleSettingItem groupTimeItem;
    //群组名称
    private SingleSettingItem groupNameItem;
    //群组二维码
    private SingleSettingItem groupQrcodeItem;
    //群组成员个数
    private SingleSettingItem groupMemberCount;
    //群组公告
    private SingleSettingItem singleSettingItem;
    //群组申请加入的按钮
    private Button applyButton;
    //群组详情
    private GroupInfo groupInfo;
    private boolean needApply; //判断是不是自己的群组 是 发消息   不是 申请加入

    //群组成员
    private ExpandRecyclerView membersContainer;
    //群组成员适配器
    private RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder> groupMemberAdapter;
    //群组成员
    private ArrayList<GroupMember> groupMembers;
    //显示的数量
    private int spanCount = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        //获取群组的详情
        groupInfo = intent.getParcelableExtra(SString.GROUP_PRE);
        needApply = intent.getBooleanExtra(SString.TYPE, false);
        if (groupInfo == null) {
            finish();
            return;
        }
        //群组Id为空或不存在
        if (TextUtils.isEmpty(groupInfo.getG_id())) {
            finish();
            return;
        }

        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, groupInfo.getG_name(), null, -1, this);


        initData();
        initView();
        asynData();
        logicControlView();
        refreshData();
    }

    //群组列表界面需要刷新的控件可以在此进行添加
    private void refreshData() {
        if (groupInfo == null) {
            return;
        }
        applyButton.setText(needApply ? getString(R.string.apply_join_group) : getString(R.string.sendmessage));
        String g_create_time = groupInfo.getG_create_time();
        //设置群组创建时间
        if (!TextUtils.isEmpty(g_create_time)) {
            groupTimeItem.setRightText((new SimpleDateFormat(getString(R.string.group_create_time_format))).format(new Date(Long.valueOf(g_create_time) * 1000)));
        }

        //群组图像
        Glide.with(this)
                .load(ShareLockManager.getImgUrl(groupInfo.getG_header()))
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(groupIconItem.getRightImage());
        //群组名称
        String name = groupInfo.getG_name();
        if (TextUtils.isEmpty(name)) {
            name = SString.GROUP_PRE + groupInfo.getG_id();
        }
        groupNameItem.setRightText(name);

        //群组公告
        String declare = groupInfo.getG_declared();
        if (TextUtils.isEmpty(declare)) {
            declare = "";
        }
        singleSettingItem.setRightText(declare);

        if (groupMemberAdapter != null && groupMembers != null) {
            int count = groupMembers.size();
            groupMemberCount.setRightText(getString(R.string.group_member_count_, String.valueOf(count)));
            //只显示前四位成员  如果大于四位成员  则截断
            //刷新数据
            groupMemberAdapter.fillList(count > spanCount ? groupMembers.subList(0, spanCount) : groupMembers);
            //设置群组数量
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_apply_join_group;
    }


    protected void initData() {
    }

    protected void initView() {
        //
        membersContainer = (ExpandRecyclerView) findViewById(R.id.group_members);
        groupIconItem = (SingleSettingItem) findViewById(R.id.group_icon);
        //创建时间d
        groupTimeItem = (SingleSettingItem) findViewById(R.id.group_create_time);
        //设置群组名称
        groupNameItem = (SingleSettingItem) findViewById(R.id.group_name);
        //设置群组二维码
        groupQrcodeItem = (SingleSettingItem) findViewById(R.id.group_qrcode);
        //初始化右边的二维码小图片
        groupQrcodeItem.getRightImage().setImageResource(R.drawable.android_my_qrcode);
        //设置群组成员个数
        groupMemberCount = (SingleSettingItem) findViewById(R.id.group_member_count);
        //设置群组公告
        singleSettingItem = (SingleSettingItem) findViewById(R.id.group_declare);
        //申请加群
        applyButton = (Button) findViewById(R.id.group_apply_btn);
        applyButton.setOnClickListener(this);

    }


    protected void asynData() {
        showCoverLayout();
        //异步数据
        GroupService.getGroupAndMemberDetails(groupInfo.getG_id(), new GroupService.Callback<GroupInfo, ArrayList<GroupMember>>() {
            @Override
            public void success(GroupInfo s, ArrayList<GroupMember> m) {
                if (s == null || m == null) {
                    return;
                }
                closeCoverLayout();
                groupInfo = s;  //获取新的群组详情 刷新群组详情数据
                groupMembers = m;  // 获取群组成员  刷新群组成员
                refreshData();

            }

            @Override
            public void fail() {
                ToastUtil.showMessage(getString(R.string.group_details_get_err));

            }
        });


    }

    protected void logicControlView() {
        //设置布局管理器
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        membersContainer.setLayoutManager(manager);
        membersContainer.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.android_width_18);
            }
        });
        //设置适配器
        groupMemberAdapter = new RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                //创建布局
                return new RecycleViewBaseHolder(parent, R.layout.item_group_details_group_member);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                //绑定数据
                GroupMember item = getItem(position);
                if (item == null) {
                    return;
                }
                //显示添加成员的加号
                if (item.getU_username().equals(SString.GROUP_ADD_FLAG)) {
                    holder.setImageResource(R.id.group_member_icon, R.drawable.add_group_member_icon);
                    holder.setText(R.id.group_member_name, "");
                    return;
                }
                //群名片
                String name = item.getGur_card();
                if (TextUtils.isEmpty(name)) {
                    //账号
                    name = item.getU_username();
                }
                holder.setText(R.id.group_member_name, name);
                //成员图像
                String url = item.getU_header();
                if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                    url = Url.CORE_IP + url;
                }
                holder.setImageResource(R.id.group_member_icon, url);
                //成员的角色
                int gur_identity = item.getGur_identity();
                String role = null;
                TextView roleTv = holder.getView(R.id.group_member_role);
                switch (gur_identity) {
                    case 0:
                        break;
                    case 1:
                        role = getString(R.string.group_role_admin);
                        roleTv.setBackgroundResource(R.drawable.btn_style_green_selecor);
                        break;
                    case 2:
                        role = getString(R.string.group_belong);
                        roleTv.setBackgroundResource(R.drawable.btn_style_orage_selecor);
                        break;
                    default:
                        break;
                }
                if (!TextUtils.isEmpty(role)) {
                    roleTv.setVisibility(View.VISIBLE);
                    roleTv.setText(role);
                } else {
                    roleTv.setVisibility(View.GONE);
                }

            }
        };
        //设置适配器
        membersContainer.setAdapter(groupMemberAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.group_apply_btn: //申请加群入口
                if (!needApply) {
                    ToastUtil.showMessage(getString(R.string.sendmessage));
                    ShareLockManager.startGroupChattingActivity(mContext, groupInfo.getG_id(), groupInfo.getG_name(), true);
                    return;
                }
                showDiglog();
                GroupMemberService.proposeJoin(groupInfo.getG_id(), "", new GroupMemberService.Callback<String>() {
                    @Override
                    public void success(String s) {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.apply_success), Toast.LENGTH_LONG);
                        finish();
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.apply_fail), Toast.LENGTH_LONG);
                    }
                });
                break;
        }
    }
}
