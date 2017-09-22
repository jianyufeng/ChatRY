package com.sanbafule.sharelock.comm.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.UI.contact.ContactInfoActivity;
import com.sanbafule.sharelock.UI.group.GroupDetailsActivity;
import com.sanbafule.sharelock.UI.group.GroupMemberDetailsActivity;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.activity.ImageGralleryPagerActivity;
import com.sanbafule.sharelock.chatting.activity.ShootVideoActivity;
import com.sanbafule.sharelock.chatting.modle.ViewImageInfo;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferenceSettings;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferences;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.ContactObserve;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupMemberSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.Code;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;

import java.io.InvalidClassException;
import java.util.ArrayList;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MentionedInfo;

/**
 * Created by Administrator on 2016/10/25.
 */
public class ShareLockManager {


    private ClientUser clientUser;

    public static ShareLockManager instance;


    private ShareLockManager() {

    }

    public static ShareLockManager getInstance() {
        if (instance == null) {
            instance = new ShareLockManager();
        }
        return instance;
    }

    public void setClientUser(ClientUser clientUser) {
        ShareLockManager.getInstance().clientUser = clientUser;
    }

    public ClientUser getClentUser() {
        if (clientUser != null) {
            return clientUser;
        }
        String registAccount = getClientUserFromSP();
        if (!TextUtils.isEmpty(registAccount)) {
            clientUser = new ClientUser();
            return clientUser.from(registAccount);
        }
        return null;

    }

    private static String getClientUserFromSP() {
        SharedPreferences sharedPreferences = ShareLockPreferences.getSharedPreferences();
        ShareLockPreferenceSettings clientuser = ShareLockPreferenceSettings.CLIENTUSER;
        String clientuserString = sharedPreferences.getString(clientuser.getId(), (String) clientuser.getDefaultValue());
        return clientuserString;
    }

    public boolean isLogin() {
        SharedPreferences sp = ShareLockPreferences.getSharedPreferences();
        ShareLockPreferenceSettings islogin = ShareLockPreferenceSettings.ISLOGIN;
        return sp.getBoolean(islogin.getId(), (boolean) islogin.getDefaultValue());
    }


    public Context getContext() {
        return SApplication.getInstance();
    }

    public String getToken() {
        if (getClentUser() != null) {
            return getClentUser().getToken();
        }
        return "";
    }

    public void saveClientUser(ClientUser user) {
        setClientUser(user);
        try {
            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.CLIENTUSER,
                    user.toString(), true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户的登录版本
     *
     * @return
     */
    public int getLoginVersion() {
        if (getClentUser() != null) {
            return getClentUser().getLoginVersion();
        }
        return 0;
    }


    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        if (getClentUser() != null) {
            return getClentUser().getU_username();
        }
        return "";
    }

    public void clearUserInfo() {
        setClientUser(null);
        try {
//            if (ShareLockManager.getInstance().isLogin()) {
//                ContactSqlManager.unregisterMsgObserver(ContactObserve.getInstance());
//            }
            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.CLIENTUSER,
                    "", true);
            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.ISLOGIN,
                    false, true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量查看图片
     *
     * @param ctx
     * @param position
     * @param session
     */
    public static void startChattingImageViewAction(Context ctx, int position, ArrayList<ViewImageInfo> session) {
        Intent intent = new Intent(ctx, ImageGralleryPagerActivity.class);
        intent.putExtra(ImageGralleryPagerActivity.EXTRA_IMAGE_INDEX, position);
        intent.putParcelableArrayListExtra(ImageGralleryPagerActivity.EXTRA_IMAGE_URLS, session);
        ctx.startActivity(intent);
    }

    /**
     * 打开群组聊天界面
     *
     * @param ctx
     * @param groupId  //群组 id
     * @param name     昵称  暂时未使用
     * @param clearTop GroupChattingActivity 返回到 MainActivity 设置到
     */
    public static void startGroupChattingActivity(Context ctx, String groupId, String name, boolean clearTop) {
        if (ctx != null && !TextUtils.isEmpty(groupId)) {
            Intent intent = new Intent(ctx, GroupChattingActivity.class).putExtra(SString.TARGETNAME, addGroupPre(groupId));
            if (clearTop) {
                ctx.startActivities(new Intent[]{new Intent(ctx, MainActivity.class), intent});
            } else {
                ctx.startActivity(intent);
            }
        }

    }


    /**
     * 打开个人聊天界面
     *
     * @param ctx
     * @param account  //群组 id
     * @param name     昵称  暂时未使用
     * @param clearTop GroupChattingActivity 返回到 MainActivity 设置到
     */
    public static void startChattingActivity(Context ctx, String account, String name, boolean clearTop) {
        if (ctx != null && !TextUtils.isEmpty(account)) {
            Intent intent = new Intent(ctx, GroupChattingActivity.class).putExtra(SString.TARGETNAME, account);
            if (clearTop) {
                ctx.startActivities(new Intent[]{new Intent(ctx, MainActivity.class),
                        intent});
            } else {
                ctx.startActivity(intent);
            }
        }

    }

    /**
     * 打开群组成员详情界面
     *
     * @param ctx
     * @param name userName
     */
    public static void startContactInfoActivity(Context ctx, String name) {
        ctx.startActivity(new Intent(ctx, ContactInfoActivity.class)
                .putExtra(SString.NAME, name));
    }

    /**
     * 打开好友详情界面
     *
     * @param ctx
     * @param groupId //会话id
     * @param account 成员
     */
    public static void startGpMbDtlActy(Context ctx, String groupId, String account) {
        ctx.startActivity(new Intent(ctx, GroupMemberDetailsActivity.class)
                .putExtra(SString.GROUP_ID, subGroupId(groupId))
                .putExtra(SString.GROUP_MEMBER_ACCOUNT, account));
    }

    //将GROUP_  删除
    public static String subGroupId(String groupId) {
        if (groupId.startsWith(SString.GROUP_PRE)) {
            groupId = groupId.substring(SString.GROUP_PRE.length());
        }
        return groupId;
    }

    //将GROUP_  删除
    public static String addGroupPre(String groupId) {
        if (!groupId.startsWith(SString.GROUP_PRE)) {
            groupId = SString.GROUP_PRE + groupId;
        }
        return groupId;
    }

    public static String getImgUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        return url;
    }

    /**
     * 打开群组详情界面
     *
     * @param ctx
     * @param groupId //会话id
     */
    public static void startGroupDetailsActivity(Context ctx, String groupId) {

        ctx.startActivity(new Intent(ctx, GroupDetailsActivity.class).putExtra(SString.GROUP_ID, subGroupId(groupId)));
    }

    public static boolean isGroupChat(String targetId) {
        if (targetId.startsWith(SString.GROUP_PRE)) {
            return true;
        }
        return false;
    }
    //启动录制小视频
    public void startRecodeVideoActivity(Activity activity) {
        activity.startActivityForResult(new Intent(activity, ShootVideoActivity.class), Code.Permission_Video_Code);
    }

    // 处理@someone
    public MentionedInfo getAt(ArrayList<String> atList, String s) {

        // 1 截取第一个@与最后一个 8197之间的字符串   因为中间包含 @字符串
        // 2 使用 9187 分割字符串  每一部分包含  @字符串
        // 3 截取每一部分中 @ 后面的字符串
        // 4 比较获取需要 @的人
        if (TextUtils.isEmpty(s) || atList.size() == 0) {

        } else {
            String msg = s;
            //1
            //截取掉@之前的字符串
            int atBefore = msg.indexOf("@");
            if (atBefore != -1) {
                msg = msg.substring(atBefore);
            }
            String reg = String.valueOf((char) 8197);
            //截取掉 8197 后的字符串
            int last8197 = msg.lastIndexOf(reg);
            if (last8197 != -1) {
                msg = msg.substring(0, last8197);
            }
            //2
            String[] split = msg.split(reg);
            //3
            if (split != null && split.length > 0) {
                ArrayList<String> containAt = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    int lastIndexOf = split[i].lastIndexOf("@");
                    if (lastIndexOf != -1) {
                        containAt.add(split[i].substring(lastIndexOf + 1));
                    }
                }
                // 4
                if (containAt.size() > 0) {
                    ArrayList<String> at = new ArrayList<>();
                    for (int i = 0; i < containAt.size(); i++) {
                        String some = containAt.get(i);
                        if (atList.contains(some)) {
                            if (some.equals(SApplication.getInstance().getString(R.string.at_all))) {
                                return new MentionedInfo(MentionedInfo.MentionedType.ALL, null, s);
                            }
                            at.add(some);
                        }
                    }
                    if (at != null && at.size() > 0) {
                        return new MentionedInfo(MentionedInfo.MentionedType.PART, at, s);
                    }
                }
            }
        }
        return null;
    }


    public void logout() { //登出 释放相关占用的资源  数据库等  清除登录的记录
        ShareLockManager.getInstance().clearUserInfo();
//        ContactSqlManager.deleteAll();
        ContactSqlManager.unregisterMsgObserver(ContactObserve.getInstance());
        ContactSqlManager.reset();
        ContactInfoSqlManager.reset();
        GroupSqlManager.reset();
        GroupInfoSqlManager.reset();
        GroupMemberSqlManager.reset();
        GroupInfoSqlManager.reset();
        RongIMClient.getInstance().logout();
    }
}
