package com.sanbafule.sharelock.UI.user;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/7/6.
 */
public class QRCodeActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.img_erweima)
    ImageView qrcode;
    @Bind(R.id.head_img)
    ImageView userIcon;
    @Bind(R.id.tv_address)
    TextView address;
    @Bind(R.id.tv_nickname)
    TextView userName;
    @Bind(R.id.sex)
    ImageView sexIg;
    ClientUser clientUser;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_erwema;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.change_user_qrcode), null, -1, this);
        initData();
        initView();
        asynData();
        refreshData();
    }

    private void refreshData() {
        //自己的图像
        String url = clientUser.getU_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        Glide.with(this)
                .load(url)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(userIcon);
        //自己的昵称
        String u_nickname = clientUser.getU_nickname();
        String u_username = clientUser.getU_username();
        if (TextUtils.isEmpty(u_nickname)) {
            u_nickname = u_username;
        }
        userName.setText(u_nickname);

        //用户的类型
        String user_type = clientUser.getUser_type();
        if (SString.EMPLOYEES.equals(user_type)) {
            user_type = getString(R.string.qrcode_user_employees);
        } else {
            user_type = getString(R.string.qrcode_user_member);

        }
        address.setText(user_type);

        //性别
        int u_sex = clientUser.getU_sex();
        String sex = null;
        switch (u_sex) {
            case 1:
                sex = getString(R.string.qrcode_user_sex_male);
                sexIg.setBackgroundColor(Color.BLACK);
                break;
            case 2:
                sexIg.setBackgroundColor(Color.RED);

                sex = getString(R.string.qrcode_user_sex_female);
                break;
            default:
                sexIg.setBackgroundColor(Color.GRAY);
                sex = getString(R.string.qrcode_user_sex_hidden);
                break;

        }
        //获取到的二维码
        String u_qr_code = clientUser.getU_qr_code();
        if (!TextUtils.isEmpty(u_qr_code) && !u_qr_code.startsWith(Url.CORE_IP)) {
            u_qr_code = Url.CORE_IP + u_qr_code;
        }
        Glide.with(this)
                .load(u_qr_code)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(qrcode);
    }

    private void asynData() {
        showCoverLayout();
        GroupService.getQrcode(String.valueOf(clientUser.getU_id()), new GroupService.Callback<String, Void>() {
            @Override
            public void success(String s, Void aVoid) {
                closeCoverLayout();
                if (!TextUtils.isEmpty(s)) {
                    clientUser.setU_qr_code(s);
                }
                refreshData();
            }

            @Override
            public void fail() {

            }
        });
    }

    private void initData() {
        clientUser = ShareLockManager.getInstance().getClentUser();
    }

    private void initView() {
    }

    @Override
    public void onClick(View v) {
             /*返回按钮点击事件*/
        switch (v.getId()) {
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
        }
    }
}
