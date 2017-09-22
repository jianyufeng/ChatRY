package com.sanbafule.sharelock.UI.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 编辑备注
 */
public class EditConfigActivity extends BaseActivity implements View.OnClickListener {



    @Bind(R.id.clear_edittext)
    ShareLockClearEditText clearEdittext;
    @Bind(R.id.text_number)
    TextView textNumber;
    @Bind(R.id.hideSoft)
    LinearLayout hideSoft;
    private String commentName;
    private Intent intent;
    private String contact_name;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.commentName, R.string.complete, -1, this);
        initData();
        initView();
    }

    private void initData() {
        intent = getIntent();
        contact_name = intent.getStringExtra(SString.CONTACT_NAME);
        commentName = intent.getStringExtra(SString.COMMENT_NAME);
        if (!MyString.hasData(contact_name) ||MyString.hasData(commentName)) {
            return;
        }
    }

    private void initView() {
        hideSoft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });

        clearEdittext.findFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity
            public void run() {
                toggleSoftInput();
            }

        }, 500);
        clearEdittext.setText(commentName);
        textNumber.setText(commentName.length() + "/10");
        clearEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = clearEdittext.getText();
                int len = editable.length();

                if (len > 10) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, 10);
                    clearEdittext.setText(newStr);
                    editable = clearEdittext.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);

                }

                textNumber.setText(len + "/10");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearEdittext.setSelection(clearEdittext.getText().toString().length());

    }


    @OnClick({R.id.left, R.id.right_text})
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                // 提交备注
                showDiglog();
                final String comment = clearEdittext.getText().toString().trim();
                if (MyString.hasData(comment) && !comment.equals(commentName)) {
                    ArrayMap<String,String> map=new ArrayMap();
                    map.put("userName", ShareLockManager.getInstance().getUserName());
                    map.put("friendsUserName","");
                    map.put("note",comment);
                    try {
                        HttpBiz.httpPostBiz(Url.UPDATE_NOTE, map, new HttpInterface() {
                            @Override
                            public void onFailure() {
                                closeDialog();
                                ToastUtil.showMessage(R.string.net);
                            }

                            @Override
                            public void onSucceed(String s) {
                                closeDialog();
                                ContactSqlManager.updateNote(contact_name,comment);
                                finish();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    ToastUtil.showMessage("修改内容为空或未修改");
                }
        }

    }

}
