package com.sanbafule.sharelock.business;


import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.ThreadPool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/20 17:06
 * cd : 三八妇乐
 * 描述：
 */
public class GroupInfoBiz {

    public GroupInfoBiz() {

    }
    public static void  getGroupInfo(String groupId, final GetGroupInfoListener listener)  {
        ArrayMap<String,String> map=new ArrayMap<>();
        map.put("g_id",groupId);
        try {
            HttpBiz.httpPostWithRESTFulBiz(Url.GET_GROUP_INFO, map, new HttpInterface() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSucceed(String s) {
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONObject result = SString.getResult(object);
                            Gson gson = new Gson();
                            final GroupInfo info = gson.fromJson(result.toString(), GroupInfo.class);
                            if(info!=null){
                                listener.getGroupInfo(info);
                                ThreadPool.getInstnce().saveGroupInfo(info);
                            }
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
}
