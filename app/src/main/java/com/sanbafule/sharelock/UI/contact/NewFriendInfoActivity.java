package com.sanbafule.sharelock.UI.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewFriendInfoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.contact_heard)
    ImageView contactHeard;
    @Bind(R.id.g)
    ImageView g;
    @Bind(R.id.contact_nickname)
    TextView contactNickname;
    @Bind(R.id.contact_username)
    TextView contactUsername;
    @Bind(R.id.lv)
    ImageView lv;
    @Bind(R.id.layout1)
    RelativeLayout layout1;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.layout4)
    LinearLayout layout4;
    @Bind(R.id.signature)
    TextView signature;
    @Bind(R.id.layout5)
    LinearLayout layout5;
    @Bind(R.id.layout3)
    LinearLayout layout3;
    @Bind(R.id.button)
    Button button;

    private Intent intent;
    private ContactInfo info;
    private    ArrayMap<String,String>map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.newfirend), null, -1, this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        info = intent.getParcelableExtra(SString.CONTACTINFO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        if (info == null) {
            return;
        }

        if (!TextUtils.isEmpty(info.getU_header())) {
            Glide.
                    with(this).
                    load(ShareLockManager.getImgUrl(info.getU_header())).
                    error(R.mipmap.ic_launcher).
                    fitCenter().
                    crossFade(300).
                    into(contactHeard);
        } else {
            contactHeard.setImageResource(R.mipmap.ic_launcher);
        }

        if (!TextUtils.isEmpty(info.getU_nickname())) {
            contactNickname.setText(info.getU_nickname());
        } else {
            contactNickname.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(info.getU_username())) {
            contactUsername.setText(info.getU_username());
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_friend_info;
    }

    @OnClick({R.id.contact_heard, R.id.layout4, R.id.layout5, R.id.layout3, R.id.button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                finish();
                break;
            case R.id.contact_heard:
                break;
            case R.id.layout4:
                break;
            case R.id.layout5:
                break;
            case R.id.layout3:
                break;
            case R.id.button:
                // 添加好友
                addFriend();
                break;
        }
    }

    private void addFriend() {
        showDiglog();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("claimantUserName", ShareLockManager.getInstance().getUserName());
        map.put("friendsUserName", info.getU_username());
        try {
            HttpBiz.httpPostBiz(Url.ADD_FRIEND_REQUEST, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    ToastUtil.showMessage(NewFriendInfoActivity.this.getString(R.string.net));
                    showDiglog();
                }

                @Override
                public void onSucceed(String s) {
                    showDiglog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            ToastUtil.showMessage("发送成功,等待验证");
                            finish();
                        } else {
                            ToastUtil.showMessage(SString.getMessage(object));
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
