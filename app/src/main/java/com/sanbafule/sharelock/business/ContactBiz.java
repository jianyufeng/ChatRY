package com.sanbafule.sharelock.business;

import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/5 11:40
 * cd : 三八妇乐
 * 描述：
 */
public class ContactBiz {


    // 下载联系人列表
    public static void getContactList(){
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpGetBiz(Url.GET_CONTACT_LIST, map, new HttpInterface() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSucceed(String s) {


                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONObject r = SString.getResult(object);
                            JSONArray friendData = r.getJSONArray("data");
                            Gson gson = new Gson();
                            List<Contact> contacts = gson.fromJson(friendData.toString(), new TypeToken<List<Contact>>() {
                            }.getType());
                            ContactSqlManager.insertContacts(contacts);
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
