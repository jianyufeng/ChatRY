package com.sanbafule.sharelock.business;

import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/16.
 */
public class ContactInfoBiz {


    public static void getContactInfo(String name, final GetContactInfoListener listener) {

        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("u_username", name);
        try {
            HttpBiz.httpGetBiz(Url.GET_CONTACTINFO, map, new HttpInterface() {
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
                            final ContactInfo info = gson.fromJson(result.toString(), ContactInfo.class);
                            if (info != null) {
                                ContactInfoSqlManager.insertContactInfo(info);
                                if (listener != null) {
                                    listener.getContactInfo(info);
                                }
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
