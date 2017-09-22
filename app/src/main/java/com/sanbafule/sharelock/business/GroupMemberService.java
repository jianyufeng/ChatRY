package com.sanbafule.sharelock.business;


import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.GroupMemberSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.BanMember;
import com.sanbafule.sharelock.modle.GroupInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.rong.imlib.model.Conversation;

/**
 * 作者:Created by 简玉锋 on 2016/12/16 14:01
 * 邮箱: jianyufeng@38.hn
 * 群组成员的网络相关的操作
 */

public class GroupMemberService {

    public static final String TAG = "GroupMemberService";
    private static GroupMemberService sInstence;

    public static GroupMemberService getInstance() {
        if (sInstence == null) {
            sInstence = new GroupMemberService();
        }
        return sInstence;
    }

    /**
     * 邀请成员加入群组
     *
     * @param groupInfo 群组
     * @param userName  被邀请的用户帐号
     */
    public static void inviteMembers(GroupInfo groupInfo, String userName, final Callback<String> callback) {

        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupInfo.getG_id());
        paras.put("groupName", groupInfo.getG_name());
        paras.put("userName", userName);           //邀请的人
        paras.put("inviter", ShareLockManager.getInstance().getUserName()); //自己
        try {
            HttpBiz.httpPostBiz(Url.INVITE_MEMBERS, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 回复邀请
     *
     * @param isAgree   是否同意 0-不同意 1-同意
     * @param requestId 请求id（客户端在消息返回中的extra属性中获取）
     */
    public static void replyInvite(Boolean isAgree, String requestId, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("isAgree", String.valueOf(isAgree ? 1 : 0));
        paras.put("requestId", requestId);

        try {
            HttpBiz.httpPostBiz(Url.REPLY_INVITE, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }


    /**
     * 用户申请加入群组
     *
     * @param groupId 群组id
     * @param note    请求备注
     */
    public static void proposeJoin(String groupId, String note, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("userName", ShareLockManager.getInstance().getUserName());
        paras.put("gid", groupId);
        if (!TextUtils.isEmpty(note)) {
            paras.put("note", note);
        }

        try {
            HttpBiz.httpPostBiz(Url.PROPOSE_JOIN, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 同意/拒绝用户加入群组申请
     *
     * @param isAgree   是否同意 0-不同意 1-同意
     * @param requestId 请求id（客户端在消息返回中的extra属性中获取）
     */
    public static void replyPropose(Boolean isAgree, String requestId, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("isAgree", String.valueOf(isAgree ? 1 : 0));
        paras.put("requestId", requestId);
        try {
            HttpBiz.httpPostBiz(Url.REPLY_PROPOSE, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 群组设置管理员
     *
     * @param groupId  群组id
     * @param userName 用户账号
     */
    public static void setGroupAdmin(final String groupId, final String userName, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("userName", userName);
        try {
            HttpBiz.httpPostBiz(Url.SET_GROUP_ADMIN, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                            GroupMemberSqlManager.updateRole(groupId, userName, 1);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 群组取消管理员
     *
     * @param groupId  群组id
     * @param userName 用户账号
     */
    public static void cancleGroupAdmin(final String groupId, final String userName, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("userName", userName);
        try {
            HttpBiz.httpPostBiz(Url.CANCEL_GROUP_ADMIN, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                            GroupMemberSqlManager.updateRole(groupId, userName, 0);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 群组成员禁言
     *
     * @param groupId  群组id
     * @param userName 被禁言的用户帐号
     * @param minute   禁言时长（分钟单位）
     */
    public static void setGroupMenmberBan(String groupId, String userName, Long minute, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("groupId", groupId);
        paras.put("userName", userName);
        paras.put("minute", String.valueOf(minute));
        try {
            HttpBiz.httpPostBiz(Url.BAN_MEMBER, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 解除禁言群成员
     *
     * @param groupId  群组id
     * @param userName 被禁言的用户帐号
     */
    public static void cancelGroupMenmberBan(String groupId, String userName, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("groupId", groupId);
        paras.put("userName", userName);
        try {
            HttpBiz.httpPostBiz(Url.CANCEL_BAN_MEMBER, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 查询禁言群成员
     *
     * @param groupId 群组id
     */
    public static void checkBanMenmbers(String groupId, final Callback<ArrayList<BanMember>> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("groupId", groupId);
        try {
            HttpBiz.httpPostBiz(Url.CHECK_BAN_MEMBERS, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);
                            JSONArray array = result.getJSONArray("users");
                            ArrayList<BanMember> banMembers = null;
                            if (array.length() > 0) {
                                TypeToken<ArrayList<BanMember>> typeToken = new TypeToken<ArrayList<BanMember>>() {
                                };
                                banMembers = new Gson().fromJson(array.toString(), typeToken.getType());
                            }
                            callback.success(banMembers);
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 用户退出群组
     *
     * @param groupId 群组id   用户帐号
     * @param
     */
    public static void quitGroup(final String groupId, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("u_username", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpPostBiz(Url.QUIT_GROUP, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);

                            //删除群组
                            GroupSqlManager.deleteGroup(groupId);
                            //删除消息
                            //删除会话
                            GroupService.deleteMessage(Conversation.ConversationType.GROUP,
                                    ShareLockManager.addGroupPre(groupId));
                            SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_QUIT_GROUP)
                                    .putExtra(SString.GROUP_ID, groupId));
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 解散群组
     * 解散群（群主）
     *
     * @param groupId 群组id       操作人帐号
     */
    public static void DismissGroup(final String groupId, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("userName", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpPostBiz(Url.DISMISS_GROUP, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                            //解散群组 删除数据库中的群组
                            GroupSqlManager.deleteGroup(groupId);
                            GroupService.deleteMessage(Conversation.ConversationType.GROUP,
                                    ShareLockManager.addGroupPre(groupId));
                            //发送删除群组通知
                            SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_DISMISS_GROUP)
                                    .putExtra(SString.GROUP_ID, groupId));
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    /**
     * 踢出群组
     *
     * @param groupId  群组id
     * @param userName 被踢出的用户帐号
     */
    public static void KickGroup(final String groupId, final String userName, final Callback<String> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("groupId", groupId);
        paras.put("userName", userName);
        try {
            HttpBiz.httpPostBiz(Url.KICK_GROUP, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            callback.success(null);
                            GroupMemberSqlManager.delMember(groupId, userName);
                            SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_REMOVE_MEMBER)
                                    .putExtra(SString.MEMBER, userName));
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();

        }

    }

    public interface Callback<T> {
        void success(T t);

        void fail();

    }
}

