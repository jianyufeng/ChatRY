package com.sanbafule.sharelock.UI.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.util.PatternUtils;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

/**
 * 作者:Created by 简玉锋 on 2016/12/5 10:10
 * 邮箱: jianyufeng@38.hn
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    //原错误提示
    private TextView oldTip;
    //密码
    private ShareLockClearEditText oldPassWord;

    //新密码
    private ShareLockClearEditText inputPassWord;
    //确认密码
    private ShareLockClearEditText repeatPassWord;
    //新密码错误提示
    private TextView inputTip;
    //确认密码错误提示
    private TextView repeatTip;


    private View canShow; //明文
    private CheckBox changeCanShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.reset_password), getString(R.string.create_group_confirm), -1, this);
        initView();
    }

    private void initView() {
        //旧密码
        oldTip = (TextView) findViewById(R.id.check_password_tip);
        oldPassWord = (ShareLockClearEditText) findViewById(R.id.old_password);
        oldPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldTip.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //新密码
        inputPassWord = (ShareLockClearEditText) findViewById(R.id.input_password);
        inputTip = (TextView) findViewById(R.id.input_password_tip);
        inputPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputTip.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //确认密码
        repeatPassWord = (ShareLockClearEditText) findViewById(R.id.repeat_password);
        repeatTip = (TextView) findViewById(R.id.repeat_password_tip);
        repeatPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repeatTip.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //点击显示明文
        canShow = findViewById(R.id.can_show);
        canShow.setOnClickListener(this);
        changeCanShow = (CheckBox) findViewById(R.id.change_can_show);
        changeCanShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //选择状态 显示明文--设置为可见的密码
                    oldPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inputPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    repeatPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    oldPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    repeatPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                oldPassWord.setSelection(oldPassWord.getText().toString().length());
                inputPassWord.setSelection(inputPassWord.getText().toString().length());
                repeatPassWord.setSelection(repeatPassWord.getText().toString().length());

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_original_password;
    }


    private void setTip(String msg) {
        oldTip.setText(msg);
        oldTip.setVisibility(View.VISIBLE);

    }

    private void setInputTip(String msg) {
        inputTip.setText(msg);
        inputTip.setVisibility(View.VISIBLE);
    }

    private void setRepeatTip(String msg) {
        repeatTip.setText(msg);
        repeatTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                //返回键
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text://确定键
                String oldPassword = oldPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)) {
                    setTip(getString(R.string.un_input_pass));
                    return;
                }
                if (oldPassword.length() < 6 || oldPassword.length() > 16) {
                    setTip(getString(R.string.length_err));
                    return;
                }
                if (!PatternUtils.isShuZiYing(oldPassword)) {  //数字英文 及下划线
                    setTip(getString(R.string.format_err));
                    return;
                }
                String word = inputPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(word)) {
                    setInputTip(getString(R.string.input_pass));
                    return;
                }
                if (word.length() < 6 || word.length() > 16) {
                    setInputTip(getString(R.string.length_err));
                    return;
                }
                if (!PatternUtils.isShuZiYing(word)) {  //数字英文 及下划线
                    setInputTip(getString(R.string.format_err));
                    return;
                }
                String repeatWord = repeatPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(repeatWord)) {
                    setRepeatTip(getString(R.string.un_repeat_pass));
                    return;
                }
                if (repeatWord.length() < 6 || repeatWord.length() > 16) {
                    setRepeatTip(getString(R.string.length_err));
                    return;
                }
                if (!PatternUtils.isShuZiYing(repeatWord)) {  //数字英文 及下划线
                    setRepeatTip(getString(R.string.format_err));
                    return;
                }
                if (!word.equals(repeatWord)) {
                    setRepeatTip(getString(R.string.repeat_pass_err));
                    return;

                }
                showDiglog();
                //访问接口修改成功后跳转到登录界面
                GroupService.resetPassword(oldPassword, repeatWord, new GroupService.Callback<String, Void>() {
                    @Override
                    public void success(String s, Void aVoid) {
                        closeDialog();
                        ShareLockManager.getInstance().logout();
                        startActivities(new Intent[]{new Intent(ResetPasswordActivity.this, MainActivity.class)
                                , new Intent(ResetPasswordActivity.this, LoginActivity.class)});
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                    }
                });
                break;
            case R.id.can_show://切换选择，明文
                changeCanShow.setChecked(!changeCanShow.isChecked());
                break;
        }
    }
}
