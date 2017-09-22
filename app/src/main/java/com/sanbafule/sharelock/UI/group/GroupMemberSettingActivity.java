package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupMemberService;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupMember;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.SingleSettingItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 作者:Created by 简玉锋 on 2016/12/1 17:21
 * 邮箱: jianyufeng@38.hn
 * 群组成员设置界面
 */
public class GroupMemberSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    //设置成员角色
    private SingleSettingItem memberAdmin;
    //设置成员禁言
    private SingleSettingItem memberBan;
    //移除成员按钮
    private Button groupRemove;
    //解散群组
    private Button groupDis;
    //退出群组
    private Button groupQuit;
    private GroupMember groupMember;
    //群组id
    private String groupId;
    //成员账号
    private String memberAccount;
    //设置项的父控件
    private View showSetting_P;
    //群组名片
    private SingleSettingItem memberCard;
    //群组角色
    private SingleSettingItem memberRole;
    //加入时间
    private SingleSettingItem joinTime;

    private int selfRole;

    private TextView noMb;
    private ContactInfo info;
    //防止死循环  check  与 setOnCheckedChangeListener
    private boolean selecState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        //获取群组id
        groupId = intent.getStringExtra(SString.GROUP_ID);
        //获取群组成员的账号
        memberAccount = intent.getStringExtra(SString.GROUP_MEMBER_ACCOUNT);
        //假如未获取到则成员详情界面关闭
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(memberAccount)) {
            finish();
            return;
        }
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,
                getString(R.string.group_member_setting), null, -1, this);


        initData();
        asynData();
        initView();
        logicControlView();


    }

    private void refreshData() {
        if (groupMember == null) {
            noMb.setVisibility(View.VISIBLE);
            findViewById(R.id.member_info_contain).setVisibility(View.GONE);

            return;
        }
        //设置群组名片
        String gur_card = groupMember.getGur_card();
        if (TextUtils.isEmpty(gur_card) && info != null) {
            if (!TextUtils.isEmpty(info.getU_nickname())) {
                gur_card = info.getU_nickname();
            } else {
                gur_card = memberAccount;

            }
        } else {
            gur_card = memberAccount;
        }
        memberCard.setRightText(gur_card);
        //设置群组角色
        int gur_identity = groupMember.getGur_identity();

        memberRole.setRightText(gur_identity == 2 ? getString(R.string.group_belong)
                : gur_identity == 1 ? getString(R.string.group_role_admin) : getString(R.string.group_member_count));
        //加入时间
        joinTime.setRightText((new SimpleDateFormat(getString(R.string.group_create_time_format)))
                .format(new Date(System.currentTimeMillis())));
        //判断当前是不是管理员
        selecState = groupMember.getGur_identity() == 1;
        memberAdmin.setChecked(selecState);
        if (memberAccount.equals(ShareLockManager.getInstance().getUserName())) {
            //是自己
            //判断自己的角色  2 解散  1 退出 0 退出  且看不见设置
            showSetting_P.setVisibility(View.GONE);
            switch (selfRole) {
                case 2:
                    showWhichButton(true, false, false);
                    break;
                case 1:
                case 0:
                    showWhichButton(false, false, true);
                    break;
            }

        } else {
            //是成员
            //判断自己和成员的角色对比   大 可以设置 可以移除成员  小看不见设置
            if (selfRole > gur_identity) {
                showSetting_P.setVisibility(View.VISIBLE);
                showWhichButton(false, true, false);
            } else {
                showSetting_P.setVisibility(View.GONE);
                showWhichButton(false, false, false);
            }

        }
    }

    public void showWhichButton(boolean dis, boolean remove, boolean quit) {
        groupDis.setVisibility(dis ? View.VISIBLE : View.GONE);
        groupQuit.setVisibility(quit ? View.VISIBLE : View.GONE);
        groupRemove.setVisibility(remove ? View.VISIBLE : View.GONE);
    }

    protected void initData() {
        info = ContactInfoSqlManager.getContactInfoFormuserName(memberAccount);
    }


    protected void initView() {
        //群名片
        memberCard = (SingleSettingItem) findViewById(R.id.member_card);
        //群组中的角色
        memberRole = (SingleSettingItem) findViewById(R.id.member_role);
        //加入时间
        joinTime = (SingleSettingItem) findViewById(R.id.member_join_time);
        //是否显示设置
        showSetting_P = findViewById(R.id.show_setting_contain);

        // 设置群组成员角色
        memberAdmin = (SingleSettingItem) findViewById(R.id.member_admin);
        memberAdmin.getCheckedTextView().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //设置群组管理员
                if (selecState == isChecked) {
                    return;
                }
                doSetMemberRole();
            }
        });

        memberBan = (SingleSettingItem) findViewById(R.id.member_ban);  //设置成员禁言
        memberBan.setOnClickListener(this);
        //解散群组
        groupDis = (Button) findViewById(R.id.group_dissolve);
        groupDis.setOnClickListener(this);
        //移除群组
        groupRemove = (Button) findViewById(R.id.member_remove);
        groupRemove.setOnClickListener(this);
        //退出群组
        groupQuit = (Button) findViewById(R.id.group_quit);
        groupQuit.setOnClickListener(this);
        noMb = (TextView) findViewById(R.id.no_mb);
    }


    protected void asynData() {
        showCoverLayout();
        //异步数据
        GroupService.getGroupAndMemberDetails(groupId, new GroupService.Callback<GroupInfo, ArrayList<GroupMember>>() {
            @Override
            public void success(GroupInfo s, ArrayList<GroupMember> m) {
                if (m == null) {
                    return;
                }
                closeCoverLayout();
                //对成员提取数据
                check(m);

            }

            @Override
            public void fail() {
                ToastUtil.showMessage(getString(R.string.group_details_get_err));
            }
        });
    }

    private void check(ArrayList<GroupMember> m) {
        for (GroupMember gb : m) {
            String u_username = gb.getU_username();
            //不是自己 且不是当前的成员
            if (!u_username.equals(memberAccount) && !u_username.equals(ShareLockManager.getInstance().getUserName())) {
                continue;
            }
            if (u_username.equals(memberAccount)) {
                //获取当前成员的信息
                groupMember = gb;
            }
            if (u_username.equals(ShareLockManager.getInstance().getUserName())) {
                selfRole = gb.getGur_identity();
            }

        }
        refreshData();
    }

    protected void logicControlView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_member_setting;

    }

    //设置管理员
    private void doSetMemberRole() {
        showDiglog();
        if (groupMember.getGur_identity() == 0) { //设置管理员
            GroupMemberService.setGroupAdmin(groupMember.getGid(), groupMember.getU_username(), new GroupMemberService.Callback<String>() {
                @Override
                public void success(String s) {
                    closeDialog();
                    //设置成功
                    ToastUtil.showMessage(getString(R.string.group_admin_set_success));
                    groupMember.setGur_identity(1);
                    refreshData();
                }

                @Override
                public void fail() {
                    closeDialog();
                    //设置失败
                    ToastUtil.showMessage(getString(R.string.group_admin_set_fail));
                    memberAdmin.setChecked(selecState);

                }
            });
        } else { //取消管理员
            GroupMemberService.cancleGroupAdmin(groupMember.getGid(), groupMember.getU_username(), new GroupMemberService.Callback<String>() {
                @Override
                public void success(String s) {
                    closeDialog();
                    ToastUtil.showMessage(getString(R.string.group_admin_cancel_success));
                    groupMember.setGur_identity(0);
                    refreshData();
                }

                @Override
                public void fail() {
                    closeDialog();
                    ToastUtil.showMessage(getString(R.string.group_admin_cancel_fail));
                    memberAdmin.setChecked(selecState);

                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.member_ban: //设置成员禁言
                startActivity(new Intent(mContext, GroupMemberBanActivity.class)
                        .putExtra(SString.GROUP_ID, groupMember.getGid())
                        .putExtra(SString.GROUP_MEMBER_ACCOUNT, groupMember.getU_username()));
                break;
            case R.id.member_remove: //移除群组成员的操作
                showDiglog();
                GroupMemberService.KickGroup(groupMember.getGid(), groupMember.getU_username(), new GroupMemberService.Callback<String>() {
                    @Override
                    public void success(String s) {
                        closeDialog();
                        finish();
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                    }
                });
                break;
            case R.id.group_quit: //退出群组
                if (selfRole == 1) {
                    showDiglog();
                    groupQuit.setEnabled(false);
                    GroupMemberService.quitGroup(groupId, new GroupMemberService.Callback<String>() {
                        @Override
                        public void success(String s) {
                            groupQuit.setEnabled(true);
                            //需要发送广播通知到该群组相关界面  关闭相关界面
                            closeDialog();
                        }

                        @Override
                        public void fail() {
                            groupQuit.setEnabled(true);
                            closeDialog();
                            ToastUtil.showMessage(getString(R.string.dismiss_group_fail));

                        }
                    });
                } else {
                    ToastUtil.showMessage(getString(R.string.dismiss_group_fail));

                }

                break;
            case R.id.group_dissolve://解散群组
                if (selfRole == 2) {
                    groupDis.setEnabled(false);
                    showDiglog();
                    GroupMemberService.DismissGroup(groupId, new GroupMemberService.Callback<String>() {
                        @Override
                        public void success(String s) {
                            closeDialog();
                            //需要发送广播通知到该群组相关界面  关闭界面
                            groupDis.setEnabled(true);
                            closeDialog();
                        }

                        @Override
                        public void fail() {
                            closeDialog();
                            groupDis.setEnabled(true);
                            closeDialog();
                            ToastUtil.showMessage(getString(R.string.dismiss_group_fail));

                        }
                    });

                } else {
                    ToastUtil.showMessage(getString(R.string.dismiss_group_fail));
                }
                break;
        }
    }
}
