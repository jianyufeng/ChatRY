package com.sanbafule.sharelock.business;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupListInfo;
import com.sanbafule.sharelock.modle.GroupMember;
import com.sanbafule.sharelock.modle.GroupTransform;
import com.sanbafule.sharelock.util.OkHttpClientManager;
import com.sanbafule.sharelock.util.ThreadPool;
import com.sanbafule.sharelock.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Request;

/**
 * 作者:Created by 简玉锋 on 2016/12/7 15:41
 * 邮箱: jianyufeng@38.hn
 * <p>
 * 群组相关的网络请求
 */

public class GroupService {
    private static final String TAG = "GroupService";

    private static GroupService sInstance;

    public static GroupService getInstance() {
        if (sInstance == null) {
            sInstance = new GroupService();
        }
        return sInstance;
    }

    /**
     * 网络获取群组列表
     */
    public static void getGroupList(final Callback<ArrayList<Group>, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gur_username", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpGetBiz(Url.GET_GROUP_LIST, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    //失败
                }

                @Override
                public void onSucceed(String s) {
                    //获取群组成功
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            JSONArray array = result.getJSONArray("data");     //判断解析
                            if (array.length() > 0) {
                                TypeToken<ArrayList<GroupListInfo>> typeToken = new TypeToken<ArrayList<GroupListInfo>>() {
                                };
                                ArrayList<GroupListInfo> groupListInfos = new Gson().fromJson(array.toString(), typeToken.getType());
                                if (groupListInfos == null || groupListInfos.isEmpty()) {
                                    return;
                                }
                                ArrayList<Group> groups = GroupTransform.GroupListInfoToGroup(groupListInfos);
                                callback.success(groups, null);
                                //异步存储数据
                                ThreadPool.getInstnce().saveGroupList(groups, GroupTransform.GroupListInfoToGroupInfo(groupListInfos));

                                // 更新公共所有群组  回调通知群组发生改变
//                                if (getInstance().mCallback != null) {
//                                    getInstance().mCallback.onSyncGroup();
//                                }
                            }

                        } else {
                            callback.fail();
                            //获取失败 可能原因参数错误

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
     * 上传图片到服务器   创建群组必须先上传图片
     */
    public static void uploadPicture(String path, final Callback callback) {
        File file = new File(path);
        if (!file.exists()) {
            ToastUtil.showMessage(SApplication.getInstance().getString(R.string.upload_picture_err));
            callback.fail();
            return;
        }
        try {
            OkHttpClientManager.postAsyn(Url.UPLOAD_PICTURE, new OkHttpClientManager.ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    callback.fail();
                }

                @Override
                public void onResponse(String s) throws JSONException {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            String original = result.getString("original");
                            if (callback != null) {
                                callback.success(original, null);
                            }
                        } else {
                            callback.fail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.fail();
                    }
                }
            }, file, "file");
        } catch (IOException e) {
            e.printStackTrace();
            callback.fail();
        }
    }

    /**
     * 创建群组
     *
     * @param groupName   群组名称
     * @param groupHeader 群组图像
     * @param callback    回调
     */
    public static void createGroup(String groupName, String groupHeader, final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("userName", ShareLockManager.getInstance().getUserName());
        paras.put("groupName", groupName);
        paras.put("groupHeader", groupHeader);
        try {
            HttpBiz.httpPostBiz(Url.CREATE_GROUP, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            String gid = result.getString("gid");
                            callback.success(gid, null);
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
     * 获取群组详情
     *
     * @param groupId
     * @param callback
     */
    public static void getGroupAndMemberDetails(String groupId, final Callback<GroupInfo, ArrayList<GroupMember>> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        try {
            HttpBiz.httpGetBiz(Url.CET_GROUP_DETAILS, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            JSONObject object = result.getJSONObject("groupInfo");
                            GroupInfo groupInfo = new Gson().fromJson(object.toString(), GroupInfo.class);
                            JSONArray array = result.getJSONArray("groupUsers");     //判断解析
                            //获取群组成员  解析
                            ArrayList<GroupMember> groupMembers = null;
                            if (array.length() > 0) {
                                TypeToken<ArrayList<GroupMember>> typeToken = new TypeToken<ArrayList<GroupMember>>() {
                                };
                                groupMembers = new Gson().fromJson(array.toString(), typeToken.getType());

                                if (groupMembers != null || !groupMembers.isEmpty()) {


                                }
                            }
                            //回调
                            callback.success(groupInfo, groupMembers);
                            //异步存储
                            ThreadPool.getInstnce().saveGroupMembersInfo(groupInfo, groupMembers);


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
     * 获取群组成员详情
     *
     * @param memberAccount
     * @param callback
     */
    public static void getMemberDetails(String memberAccount, final Callback<ContactInfo, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("u_username", memberAccount);
        try {
            HttpBiz.httpGetBiz(Url.GET_CONTACTINFO, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            //解析
                            ContactInfo contactInfo = new Gson().fromJson(result.toString(), ContactInfo.class);

                            //回调
                            callback.success(contactInfo, null);
                            //异步存储
                            ThreadPool.getInstnce().saveGroupMembersInfo(contactInfo);
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
     * 获取群组成员详情
     *
     * @param groupName g_name 群组名称
     * @param callback
     */
    public static void getGroupDetails(String groupName, final Callback<GroupInfo, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("g_name", groupName);
        try {
            HttpBiz.httpGetBiz(Url.GET_GROUP_INFO, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            //解析
                            GroupInfo groupInfo = new Gson().fromJson(result.toString(), GroupInfo.class);

                            //回调
                            callback.success(groupInfo, null);
                            //异步存储
                            ThreadPool.getInstnce().saveGroupInfo(groupInfo);
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
     * 获取群组成员详情
     *
     * @param groupName g_name 群组名称
     * @param callback
     */
    public static void searchGroup(String groupName, final Callback<ArrayList<GroupInfo>, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("g_name", groupName);
        try {
            HttpBiz.httpGetBiz(Url.SEARCH_GROUP, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            JSONObject result = SString.getResult(jsonObject);  //获取结果集合
                            //解析
                            JSONArray array = result.getJSONArray("data");
                            ArrayList<GroupInfo> groupInfos = null;
                            if (array.length() > 0) {
                                TypeToken<ArrayList<GroupInfo>> typeToken = new TypeToken<ArrayList<GroupInfo>>() {
                                };
                                groupInfos = new Gson().fromJson(array.toString(), typeToken.getType());
                            }
                            //回调
                            callback.success(groupInfos, null);
                            //异步存储
                            ThreadPool.getInstnce().saveGroupInfos(groupInfos);
                        } else {
                            onFailure();
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        onFailure();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.fail();
        }
    }

    /**
     * 更改群组名称
     *
     * @param groupId
     * @param groupName
     * @param callback
     */

    public static void changeGroupName(final String groupId, final String groupName,
                                       final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("groupName", groupName);
        try {
            HttpBiz.httpPostBiz(Url.CHANGE_GROUP_NAME, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            callback.success(groupName, null);
                            //更新群组名称
                            GroupInfoSqlManager.updateGroupInfoName(groupId, groupName);
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
     * 更改群组公告
     *
     * @param groupId
     * @param groupDeclare
     * @param callback
     */
    public static void changeGroupDeclared(final String groupId, final String groupDeclare,
                                           final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("declared", groupDeclare);
        try {
            HttpBiz.httpPostBiz(Url.CHANGE_GROUP_DECLARED, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            callback.success(groupDeclare, null);
                            //更新群组公告
                            GroupInfoSqlManager.updateGroupInfoDeclared(groupId, groupDeclare);
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
     * 更新群申请权限申请权限
     *
     * @param groupId
     * @param groupPermission 0-默认直接加入 1-需要身份验证 2-私有群
     * @param callback
     */
    public static void changeGroupPermission(final String groupId, final int groupPermission,
                                             final Callback<Integer, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("groupPermission", String.valueOf(groupPermission));
        try {
            HttpBiz.httpPostBiz(Url.CHANGE_GROUP_PERMISSION, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            callback.success(groupPermission, null);
                            //更新群组加入权限
                            GroupInfoSqlManager.updateGroupInfoPermission(groupId, groupPermission);
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
     * 更改群头像
     *
     * @param groupId
     * @param groupHeader 群头像
     * @param callback
     */
    public static void changeGroupIcon(final String groupId, String groupHeader,
                                       final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gid", groupId);
        paras.put("groupHeader", groupHeader);
        try {
            HttpBiz.httpPostBiz(Url.UPDATE_GROUP_ICON, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            JSONObject result = SString.getResult(jsonObject); //获取更新后的结果
                            String filename = result.getString("filename");
                            callback.success(filename, null);
                            //更新群组头像
                            GroupInfoSqlManager.updateGroupInfoIcon(groupId, filename);
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
     * 更改个人图像
     *
     * @param user_id   用户的 id
     * @param userHeade 用户头像临时文件名
     * @param callback
     */
    public static void changeUserInfoIcon(final String user_id, String userHeade,
                                          final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("user_id", user_id);
        paras.put("userHeader", userHeade);
        try {
            HttpBiz.httpPostBiz(Url.CHANGE_USER_ICON, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            JSONObject result = SString.getResult(jsonObject); //获取更新后的结果
                            String filename = result.getString("filename");
                            callback.success(filename, null);
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
     * 更改个人签名
     *
     * @param user_id
     * @param userSignature 个性签名
     * @param callback
     */
    public static void changeUserInfoSign(final String user_id, final String userSignature,
                                          final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("user_id", user_id);
        paras.put("userSignature", userSignature);
        try {
            HttpBiz.httpPostBiz(Url.CHANGE_USER_SING, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            callback.success(userSignature, null);
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
     * 获取用户的二维码
     *
     * @param user_id
     * @param callback
     */
    public static void getQrcode(final String user_id, final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("user_id", user_id);
        try {
            HttpBiz.httpPostBiz(Url.GET_QRCODE, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            JSONObject result = SString.getResult(jsonObject);
                            String qrCode = result.getString("qrCode");
                            callback.success(qrCode, null);
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
     * 重置密码
     *
     * @param oldPwd   原始密码
     * @param newPwd   新密码
     *                 u_username 用户帐号
     * @param callback
     */
    public static void resetPassword(final String oldPwd, String newPwd, final Callback<String, Void> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("oldPwd", oldPwd);
        paras.put("newPwd", newPwd);
        paras.put("u_username", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpPostBiz(Url.RESET_PASSWORD, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //回调
                            JSONObject result = SString.getResult(jsonObject);
                            String qrCode = result.getString("qrCode");
                            callback.success(qrCode, null);
                        } else {
                            JSONObject error = jsonObject.getJSONObject("error");
                            String message = error.getString("message");
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showMessage(message, Toast.LENGTH_LONG);
                            }
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

    public static synchronized void deleteMessage(final Conversation.ConversationType type, final String targetId) {
        //删除消息
        RongIMClient.getInstance().clearMessages(type,
                targetId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_CLEAR_MESSAGE)
                                    .putExtra(SString.TARGETNAME, targetId)
                                    .putExtra(SString.TYPE, type));
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
        //删除会话
        RongIMClient.getInstance().removeConversation(type,
                targetId, null);
    }
    public interface Callback<T, D> {
        void success(T t, D d);

        void fail();

    }
}
