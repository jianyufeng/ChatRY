package com.sanbafule.sharelock.UI.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

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
import com.sanbafule.sharelock.view.SingleSettingItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import io.rong.imlib.model.Conversation;


/**
 * Created by Administrator on 2016/7/8.
 */
public class ContactMoreActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.item_blacklist)
    SingleSettingItem itemBlacklist;
    @Bind(R.id.item_nodisturb)
    SingleSettingItem itemNodisturb;
    @Bind(R.id.item_cleanmessage)
    SingleSettingItem itemCleanmessage;
    @Bind(R.id.more_bt_dele_friend)
    Button moreBtDeleFriend;
    private Intent intent;
    private String friendName;
    private ContactInfo contacts;
    public boolean isChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.contactinfomore, -1, -1, this);
        initData();
        registerReceiver(new String[]{ReceiveAction.ACTION_DELETE_FRIEND});
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contactmore;
    }

    /***
     * 初始化数据
     */
    private void initData() {
        intent = getIntent();
        contacts = intent.getParcelableExtra(SString.CONTACTINFO);
        if (!MyString.hasData(contacts.getU_username())) {
            return;
        } else {
            friendName = contacts.getU_username();
        }

        showIsBlackList();
        showNodisturb();
        itemBlacklist.getCheckedTextView().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == isChange) {
                    return;
                }
                submitBlacklist();
            }
        });
        itemNodisturb.getCheckedTextView().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                submitNodisturb(isChecked);
            }
        });
    }

    /**
     * 消息免打扰的设置
     *
     * @param isChecked
     */
    private void submitNodisturb(boolean isChecked) {
        ContactSqlManager.setNodisturb(friendName, isChecked);
    }

    /**
     * 黑名单的加入与取消
     */
    private void submitBlacklist() {


        if (!ContactSqlManager.isBlackList(friendName)) {
            addBlackList();
        } else {
            removeBlackList();
        }
    }

    /**
     * 移除黑名单
     */
    private void removeBlackList() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("blackUserId", friendName);
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
                            itemBlacklist.setChecked(isChange);
                            ContactSqlManager.setIsBlackList(friendName, false);
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
     * 加入黑名单
     */
    private void addBlackList() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("blackUserId", friendName);
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
                            itemBlacklist.setChecked(isChange);
                            ContactSqlManager.setIsBlackList(friendName, true);
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
     * 显示免打扰
     */
    private void showNodisturb() {
        itemNodisturb.setChecked(ContactSqlManager.isNodisturb(friendName));
    }

    // 获取该用户是否是黑名单
    private void showIsBlackList() {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        map.put("friendsUserName", friendName);
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
                        ContactSqlManager.setIsBlackList(friendName, isChange);
                        itemBlacklist.setChecked(isChange);

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
     * 删除好友
     */
    private void deleteContact() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("sendUserName", ShareLockManager.getInstance().getUserName());
        map.put("deleteUserName", friendName);
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
                            ContactSqlManager.deleteContact(friendName);
                            GroupService.deleteMessage(Conversation.ConversationType.PRIVATE,
                                    friendName);
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


    @OnClick({R.id.item_cleanmessage, R.id.more_bt_dele_friend, R.id.left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_bt_dele_friend:
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
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.item_cleanmessage:
                //清除消息

                DialogUtils.showBasicWithIcon(this, R.drawable.ic_launcher, getResources().getString(R.string.hint), String.format(getResources().getString(R.string.clean_message_hint), contacts.getName()), true, new DialogUtils.DialogInterface() {
                    @Override
                    public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                        /***
                         * 清除消息
                         */
                        GroupService.deleteMessage(Conversation.ConversationType.PRIVATE, friendName);
                    }

                    @Override
                    public void onNegativeClickListener(@NonNull MaterialDialog dialog) {
                    }
                });
                break;
        }
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
                if (delFriend.equals(friendName)) {
                    finish();
                }
            }
        }
    }
}
