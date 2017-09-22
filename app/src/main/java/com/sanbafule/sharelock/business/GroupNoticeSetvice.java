package com.sanbafule.sharelock.business;

import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.InviteJoin;
import com.sanbafule.sharelock.modle.RequestJoin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 作者:Created by 简玉锋 on 2016/12/19 14:51
 * 邮箱: jianyufeng@38.hn
 */

public class GroupNoticeSetvice {
    private static final String TAG = "GroupNoticeSetvice";

    private static GroupNoticeSetvice sInstance;

    public static GroupNoticeSetvice getInstance() {
        if (sInstance == null) {
            sInstance = new GroupNoticeSetvice();
        }
        return sInstance;
    }

    /**
     * 获取加入群组请求的列表  申请加入我的群组
     * gal_reviewers 审核人
     *
     * @param callback
     */
    public static void getJoinRequestList(final Callback<ArrayList<RequestJoin>> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("gal_reviewers", ShareLockManager.getInstance().getUserName());  //审核人
        try {
            HttpBiz.httpPostWithRESTFulBiz(Url.JOIN_REQUEST_LIST, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //解析
                            JSONObject result = SString.getResult(jsonObject); //获取更新后的结果
                            JSONArray data = result.getJSONArray("data");
                            if (data.length() > 0) {
                                TypeToken<ArrayList<RequestJoin>> typeToken = new TypeToken<ArrayList<RequestJoin>>() {
                                };
                                ArrayList<RequestJoin> joinGroupRequests = new Gson().fromJson(data.toString(), typeToken.getType());
                                if (joinGroupRequests == null || joinGroupRequests.isEmpty()) {
                                    return;
                                }
                                //回调
                                callback.success(joinGroupRequests);
                            }

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
     * 获取邀请加入群组的列表  邀请我加入他的群组
     * grl_invitee  被邀请人
     *
     * @param callback
     */
    public static void getInviteRequestList(final Callback<ArrayList<InviteJoin>> callback) {
        ArrayMap<String, String> paras = new ArrayMap<>();
        paras.put("grl_invitee", ShareLockManager.getInstance().getUserName());  //被邀请人
        try {
            HttpBiz.httpPostWithRESTFulBiz(Url.INVITE_REQUEST_LIST, paras, new HttpInterface() {
                @Override
                public void onFailure() {
                    callback.fail();
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);  //提取json对象
                        if (SString.getSuccess(jsonObject)) {          //判断是否获取成功
                            //解析
                            JSONObject result = SString.getResult(jsonObject); //获取更新后的结果
                            JSONArray data = result.getJSONArray("data");
                            if (data.length() > 0) {
                                TypeToken<ArrayList<InviteJoin>> typeToken = new TypeToken<ArrayList<InviteJoin>>() {
                                };
                                ArrayList<InviteJoin> inviteJoins = new Gson().fromJson(data.toString(), typeToken.getType());
                                if (inviteJoins == null || inviteJoins.isEmpty()) {
                                    return;
                                }
                                //回调
                                callback.success(inviteJoins);
                            }

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
