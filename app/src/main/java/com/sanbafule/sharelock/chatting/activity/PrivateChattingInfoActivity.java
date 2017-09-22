package com.sanbafule.sharelock.chatting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.DatailSettingItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import io.rong.imlib.model.Conversation;


/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/30 14:19
 * cd : 三八妇乐
 * 描述：
 */
public class PrivateChattingInfoActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.contact_details)
    DatailSettingItem contactDetails;
    @Bind(R.id.chatting_top_switch)
    SwitchCompat chattingTopSwitch;
    @Bind(R.id.chatting_top)
    LinearLayout chattingTop;
    @Bind(R.id.chatting_disturb_switch)
    SwitchCompat chattingDisturbSwitch;
    @Bind(R.id.chatting_disturb)
    LinearLayout chattingDisturb;
    @Bind(R.id.chatting_file)
    LinearLayout chattingFile;
    @Bind(R.id.chatting_bg)
    TextView chattingBg;
    @Bind(R.id.chatting_search)
    TextView chattingSearch;
    @Bind(R.id.chatting_clean)
    TextView chattingClean;
    @Bind(R.id.chatting_complain)
    TextView chattingComplain;
    @Bind(R.id.chatting_blacklist_switch)
    SwitchCompat chattingBlacklistSwitch;
    @Bind(R.id.chatting_blacklist)
    LinearLayout chattingBlacklist;
    @Bind(R.id.bt_delete_friend)
    Button button;
    private String targetId;
    private Intent intent;

    private ContactInfo contactInfo;

    private boolean isChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "聊天信息", null, -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
        intent = getIntent();
        targetId = intent.getStringExtra(SString.TARGETNAME);
        if (!MyString.hasData(targetId)) return;
        //获取所有的数据
        getAllData();
        chattingTopSwitch.setOnCheckedChangeListener(this);
        chattingDisturbSwitch.setOnCheckedChangeListener(this);
        chattingBlacklistSwitch.setOnCheckedChangeListener(this);
        registerReceiver(new String[]{ReceiveAction.ACTION_DELETE_FRIEND});
    }

    private void getAllData() {
        showIsTopConversation();
        showNoDisturb();
        showIsBlackList();
    }

    /**
     * 显示是否是黑名单
     */
    private void showIsBlackList() {

        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("friendsUserName", targetId);
        try {
            HttpBiz.httpPostBiz(Url.IS_BLACK_LIST, map, new HttpInterface() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSucceed(String s) {
                    //
                    try {
                        JSONObject object = new JSONObject(s);
                        isChange = SString.getSuccess(object);
                        ContactSqlManager.setIsBlackList(targetId, isChange);
                        chattingBlacklistSwitch.setChecked(isChange);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示是否是免打扰
     */
    private void showNoDisturb() {
        chattingDisturbSwitch.setChecked(ContactSqlManager.isNodisturb(targetId));
    }

    /**
     * 显示是否是聊天置顶
     */
    private void showIsTopConversation() {

    }


    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {

        switch (view.getId()) {
            case R.id.chatting_top_switch:
                // 聊天置顶
                break;
            case R.id.chatting_disturb_switch:
                // 聊天免打扰
                ContactSqlManager.setNodisturb(targetId, isChecked);
                break;
            case R.id.chatting_blacklist_switch:
                // 设置黑名单
                if (isChecked == isChange) {
                    return;
                }
                submitBlacklist();
                break;

        }
    }

    private void submitBlacklist() {
        if (!ContactSqlManager.isBlackList(targetId)) {
            addBlackList();
        } else {
            removeBlackList();
        }

    }

    /**
     * 加入黑名单
     */
    private void addBlackList() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("blackUserId", targetId);
        try {
            HttpBiz.httpPostBiz(Url.ADD_BLACK_LIST, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();
                    ToastUtil.showMessage(R.string.net);
                }

                @Override
                public void onSucceed(String s) {
                    closeDialog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            isChange = true;
                            if (chattingBlacklistSwitch != null) {
                                chattingBlacklistSwitch.setChecked(isChange);
                            }
                            ContactSqlManager.setIsBlackList(targetId, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 移除黑名单
     */
    private void removeBlackList() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("blackUserId", targetId);
        try {
            HttpBiz.httpPostBiz(Url.REMOVE_BLACK_LIST, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();
                    ToastUtil.showMessage(R.string.net);
                }

                @Override
                public void onSucceed(String s) {
                    closeDialog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            isChange = false;
                            chattingBlacklistSwitch.setChecked(isChange);
                            ContactSqlManager.setIsBlackList(targetId, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_private_chatting_info;
    }


    @OnClick({R.id.contact_details, R.id.chatting_file, R.id.chatting_bg, R.id.chatting_search, R.id.chatting_clean, R.id.chatting_complain, R.id.bt_delete_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_details:
                // 跳转到联系人详情
                break;
            case R.id.chatting_file:
                // 聊天文件
                searchChattingFile();
                break;
            case R.id.chatting_bg:
                // 更换聊天背景
                changeChattingBackground();
                break;
            case R.id.chatting_search:
                // 搜索本回话的聊天
                intent =new Intent(this, SearchMessageActivity.class);
                intent.putExtra(SString.NAME,targetId);
                startActivity(intent);
                break;
            case R.id.chatting_clean:
                // 清空历史消息
                clearMessage();
                break;
            case R.id.chatting_complain:
                // 投诉
                break;
            case R.id.bt_delete_friend:
                // 删除好友
                DialogUtils.showBasicWithIcon(this, R.drawable.ic_launcher, R.string.hint, R.string.delete_friend, true, new DialogUtils.DialogInterface() {
                    @Override
                    public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                        // 删除好友
                        deleteContact();
                    }

                    @Override
                    public void onNegativeClickListener(@NonNull MaterialDialog dialog) {
                    }
                });
                break;
        }
    }

    /**
     * 删除好友
     */
    private void deleteContact() {

        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("sendUserName", ShareLockManager.getInstance().getUserName());
        map.put("deleteUserName", targetId);
        try {
            HttpBiz.httpPostBiz(Url.DELECT_FRIEND, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();
                    ToastUtil.showMessage(R.string.net);
                }

                @Override
                public void onSucceed(String s) {
                    closeDialog();
                    try {

                        if (SString.getSuccess(new JSONObject(s))) {
                            // 本地删除
                            ContactSqlManager.deleteContact(targetId);
                            GroupService.deleteMessage(Conversation.ConversationType.PRIVATE,
                                    targetId);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 搜索聊天内的所有文件
     */
    private void searchChattingFile() {
        startActivity(new Intent(this, ShowImageAndFileMessageActivity.class).putExtra(SString.TARGETNAME, targetId));
    }


    /**
     * 清空消息
     */
    private void clearMessage() {
        DialogUtils.showBasicWithTile(this, getString(R.string.dialog_hint_title), String.format(getString(R.string.clean_message_hint), targetId), R.string.clean, R.string.negative, true, new DialogUtils.DialogInterface() {
            @Override
            public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                GroupService.deleteMessage(Conversation.ConversationType.PRIVATE, targetId);
            }

            @Override
            public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

            }
        });


    }

    /**
     * 更换聊天背景
     */
    private void changeChattingBackground() {
        startActivity(new Intent(this, ChangeChattingBackgroundActivity.class));
    }

    /**
     * 搜索本回话内的消息
     */
    private void searchMessage() {

    }


    /**
     * 投诉
     */
    private void complain() {

    }


    /**
     * 聊天置顶
     */

    private void chattingTop() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    // 广播接收器  接受删除好友的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_DELETE_FRIEND.equals(intent.getAction())) {
            /**1  删除好友
             */
            String delFriend = intent.getStringExtra(SString.TARGETNAME);
            if (!TextUtils.isEmpty(delFriend)) {
                if (delFriend.equals(targetId)) {
                    finish();
                }
            }
        }
    }
}
