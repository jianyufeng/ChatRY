package com.sanbafule.sharelock.UI.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.ToastUtil;

import static android.app.Activity.RESULT_OK;


public class EditGroupInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText;
    private String content;
    private String title;
    private TextView ediType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //获取修改的类型
        title = intent.getStringExtra(SString.EDIT_GROUPINFO_TITLE);
        //内容
        content = intent.getStringExtra(SString.EDIT_GROUPINFO_CONTENT);
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,
                getString(R.string.edit_group_title, title),
                getString(R.string.complete), R.drawable.head_right_save_selector, this);
        initView();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.edit_tv);
        ediType = (TextView) findViewById(R.id.edit_type);
        //设置编辑的类型
        ediType.setText(title);
        if (!TextUtils.isEmpty(content)) {
            editText.setText(content);
        }
        editText.setSelection(editText.getText().length());
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_group_info;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                hideSoftKeyboard();
                String s = editText.getText().toString().trim();
                //修改群组名称的提醒
                if (title.equals(getString(R.string.group_name)) && TextUtils.isEmpty(s)) {
                    ToastUtil.showMessage(getString(R.string.edit_group_tip, title));
                    return;
                }
                if (!TextUtils.isEmpty(s) && s.equals(content)) {
                    ToastUtil.showMessage(getString(R.string.not_change));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(SString.EDIT_GROUPINFO_CONTENT, s);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
