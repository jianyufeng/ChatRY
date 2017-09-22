package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.DatailSettingItem;
import com.sanbafule.sharelock.view.SingleSettingItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * 作者:Created by 简玉锋 on 2016/11/29 17:15
 * 邮箱: jianyufeng@38.hn
 * 群组成员详情 界面
 * groupId 是不带前缀的
 */

public class GroupMemberDetailsActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    //图像  等级  管理    设置  在这一个item中设置
    private DatailSettingItem memberDetailsSettingItem;

    //设置位置
    private SingleSettingItem locationSettingItem;
    //设置个性签名
    private SingleSettingItem signSettingItem;
    // //发起会话
    private Button memberChat;
    //群组id
    private String groupId;
    //成员账号
    private String memberAccount;

    //个人信息
    private ContactInfo contactInfo;

    //加为好友
    private Button addFriend;

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
                getString(R.string.group_member_title), getString(R.string.group_member_setting), -1, this);
        //注册移除成员
        registerReceiver(new String[]{
                ReceiveAction.ACTION_REMOVE_MEMBER});
        initData();
        asynData();
        initView();
        logicControlView();
        refreshData();
    }

    private void refreshData() {
        //刷新群组成员详情
        if (contactInfo == null) {
            return;
        }
        //成员的图像  有问题
        String url = contactInfo.getU_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        Glide.with(this)
                .load(url)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(memberDetailsSettingItem.getIcon());
        //成员账号
        memberDetailsSettingItem.setAccountString(memberAccount);
        //成员昵称
        String u_nickname = contactInfo.getU_nickname();
        if (!TextUtils.isEmpty(u_nickname)) {
            //成员账号
            u_nickname = memberAccount;
        }
        memberDetailsSettingItem.setNickName(u_nickname);
        if (contactInfo.getUser_type().equals(SString.MEMBER)) {
            memberDetailsSettingItem.setManagerVisibility(false);
            //设置等级
            String user_level = contactInfo.getUser_level();
            if (!TextUtils.isEmpty(user_level)) {
                memberDetailsSettingItem.setLevelString(user_level);
            }
        } else {
            memberDetailsSettingItem.setLevelVisibility(false);
            String user_level = contactInfo.getUser_level();
            if (!TextUtils.isEmpty(user_level)) {
                memberDetailsSettingItem.setManagerString(user_level);
            }
        }
        String u_signature = contactInfo.getU_signature();
        if (!TextUtils.isEmpty(u_signature)) {
            signSettingItem.setRightText(u_signature);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_member_details;
    }


    protected void initData() {
        //成员详情
        contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(memberAccount);
    }


    protected void initView() {
        memberDetailsSettingItem = (DatailSettingItem) findViewById(R.id.member_details); //设置群组成员详情
        locationSettingItem = (SingleSettingItem) findViewById(R.id.group_location); //设置位置
        signSettingItem = (SingleSettingItem) findViewById(R.id.group_sign); //设置个性签名

        //是好友
        if (ContactSqlManager.hasThisFriend(memberAccount)) {
            memberChat = (Button) findViewById(R.id.member_chat); //发起会话
            memberChat.setOnClickListener(this);
            memberChat.setVisibility(View.VISIBLE);
        } else {
            //不是好友
            addFriend = (Button) findViewById(R.id.add_friend); //添加朋友
            addFriend.setOnClickListener(this);
            addFriend.setVisibility(View.VISIBLE);
        }
    }


    protected void asynData() {
        GroupService.getMemberDetails(memberAccount, new GroupService.Callback<ContactInfo, Void>() {
            @Override
            public void success(ContactInfo c, Void aVoid) {
                if (c == null) {
                    return;
                }
                contactInfo = c;
                refreshData();
            }

            @Override
            public void fail() {

            }
        });

    }


    protected void logicControlView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                //跳转到群组成员设置界面
                startActivity(new Intent(mContext, GroupMemberSettingActivity.class)
                        .putExtra(SString.GROUP_ID, groupId)
                        .putExtra(SString.GROUP_MEMBER_ACCOUNT, memberAccount));
                break;
            case R.id.member_chat:
                ShareLockManager.startChattingActivity(this, memberAccount, null, true);
                break;
            case R.id.add_friend: //添加新好友
                addFriend();
                break;
        }
    }

    private void addFriend() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("claimantUserName", ShareLockManager.getInstance().getUserName());
        map.put("friendsUserName", memberAccount);
        try {
            HttpBiz.httpPostBiz(Url.ADD_FRIEND_REQUEST, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();
                    ToastUtil.showMessage(getString(R.string.net));
                }

                @Override
                public void onSucceed(String s) {
                    closeDialog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            ToastUtil.showMessage(getString(R.string.conv_msg_success));
                        } else {
                            ToastUtil.showMessage(SString.getMessage(object));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            closeDialog();
            ToastUtil.showMessage(getString(R.string.conv_msg_failed));
        }
    }

    // 广播接收器  接受群组详情及群组成员插入完成的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_REMOVE_MEMBER.equals(intent.getAction())) {
            String removeMember = intent.getStringExtra(SString.MEMBER);
            if (!TextUtils.isEmpty(removeMember) && removeMember.equals(memberAccount)) {
                finish();
            }
        }
    }
}
