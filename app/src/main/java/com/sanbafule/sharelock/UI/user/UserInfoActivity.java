package com.sanbafule.sharelock.UI.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.google.gson.Gson;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.account.EditConfigActivity;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.contact.ContactSignActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.view.DatailSettingItem;
import com.sanbafule.sharelock.view.SingleSettingItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/16 13:45
 * cd : 三八妇乐
 * 描述：用户信息
 */
public class UserInfoActivity extends BaseActivity {


    @Bind(R.id.item_info)
    DatailSettingItem itemInfo;
    @Bind(R.id.contact_location)
    SingleSettingItem contactLocation;
    @Bind(R.id.contact_sign)
    SingleSettingItem contactSign;

    private String userName;
    private ClientUser clientUser;
    //个性签名
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.my_info, -1, -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
        // 拿到数据绑定数据
        Intent intent = getIntent();
        userName = intent.getStringExtra(SString.TARGETUSERID);
        clientUser = ShareLockManager.getInstance().getClentUser();
        setData();
    }

    private void setData() {
        ImageBiz.showImage(UserInfoActivity.this, itemInfo.getIcon(), clientUser.getU_header(), R.drawable.icon_touxiang_persion_gray);
        itemInfo.setNickName(clientUser.getU_nickname());
        itemInfo.setAccountString(clientUser.getU_username());
        if(MyString.hasData(clientUser.getUser_type())){
            if (clientUser.getUser_type().equals(SString.MEMBER)) {
                itemInfo.setManagerVisibility(true);
                itemInfo.setLevelVisibility(true);
                itemInfo.setLevelString(clientUser.getUser_level());
            } else if (clientUser.getUser_type().equals(SString.EMPLOYEES)) {
                itemInfo.setManagerVisibility(false);
                itemInfo.setLevelVisibility(false);
            }
        }

        contactLocation.setRightText("陕西 西安");
        sign = clientUser.getU_signature();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDiglog();
        // http请求
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("u_username", userName);
        try {
            HttpBiz.httpGetBiz(Url.GET_CONTACTINFO, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();

                }

                @Override
                public void onSucceed(String s) {
                    // 绑定数据
                    closeDialog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONObject result = SString.getResult(object);
                            Gson gson = new Gson();
                            clientUser = gson.fromJson(result.toString(), ClientUser.class);
                            setData();
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
        return R.layout.activity_user_info;
    }

    @OnClick({R.id.contact_sign})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.contact_sign:
                // 展示个性签名
                Intent intent = new Intent(this, ContactSignActivity.class);
                if (MyString.hasData(sign)) {
                    intent.putExtra(SString.CONTACT_SIGN, sign);
                } else {
                    intent.putExtra(SString.CONTACT_SIGN, "");
                }
                startActivity(intent);
                break;
        }
    }
}
