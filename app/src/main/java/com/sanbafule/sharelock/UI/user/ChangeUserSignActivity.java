package com.sanbafule.sharelock.UI.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferenceSettings;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferences;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.InvalidClassException;

/**
 * 作者:Created by 简玉锋 on 2016/12/27 16:22
 * 邮箱: jianyufeng@38.hn
 */

public class ChangeUserSignActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ChangeUserSignActivity";
    // 修改的标题
    String title;
    /*修改的信息*/
    private EditText msg;

    private ClientUser clientUser;
    private TextView number;
    private LinearLayout hideSoft;
    //个性签名
    private String u_signature;

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_user_sign;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //获取个人信息
        clientUser = ShareLockManager.getInstance().getClentUser();
        if (clientUser == null) {
            return;
        }
        title = intent.getStringExtra(SString.GROUP_PRE);
        if (TextUtils.isEmpty(title)) {
            finish();
            return;
        }
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, getString(R.string.create_group_confirm), -1, this);

        initView();
        initdata();


    }


    private void initdata() {


        //设置字符限制
        msg.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        u_signature = clientUser.getU_signature();
        if (TextUtils.isEmpty(u_signature)) {
            u_signature = "";
        }


        msg.setText(u_signature);
        msg.setSelection(msg.getText().toString().length());
        number.setText(u_signature.length() + "/30");
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = msg.getText();
                int len = editable.length();

                if (len > 30) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, 10);
                    msg.setText(newStr);
                    editable = msg.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);

                }

                number.setText(len + "/30");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initView() {
        //修改的信息组件
        msg = (EditText) findViewById(R.id.tv_message);
        hideSoft = (LinearLayout) findViewById(R.id.hideSoft);
        hideSoft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });
        msg.setFocusableInTouchMode(true);
        msg.requestFocus();
        number = (TextView) findViewById(R.id.number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //返回键
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;

            //保存键
            case R.id.right_text:
                //保存按钮出发处理
                saveUserInfo();
        }

    }


    private void saveUserInfo() {
        String msgInfo = msg.getText().toString().trim();
        if (TextUtils.isEmpty(msgInfo)) {
            ToastUtil.showMessage(getString(R.string.search_empty));
            return;
        }
        if (msgInfo.equals(clientUser.getU_header())) {
            ToastUtil.showMessage(getString(R.string.not_change));
            return;
        }
        showDiglog();
        GroupService.changeUserInfoSign(String.valueOf(clientUser.getU_id()), msgInfo, new GroupService.Callback<String, Void>() {
            @Override
            public void success(String s, Void aVoid) {
                clientUser.setU_signature(s);
                try {
                    //修改成功后保存
                    ShareLockPreferences.savePreference(ShareLockPreferenceSettings.CLIENTUSER,
                            clientUser.toString(), true);
                    ShareLockManager.getInstance().setClientUser(clientUser);
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                closeDialog();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void fail() {
                closeDialog();
                ToastUtil.showMessage(getString(R.string.change_fail));

            }
        });
    }


}
