package com.sanbafule.sharelock.UI.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.comm.SString;

import butterknife.Bind;




/**
 * 展示个性签名
 */
public class ContactSignActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_sign)
    TextView tvSign;
    private Intent intent;
    private String sign;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contactsign;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,R.string.sign,-1,-1,this);
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        if (sign == null) {
            return;
        } else {
            tvSign.setText(sign);
        }

    }

    private void initData() {
        intent = getIntent();
        sign = intent.getStringExtra(SString.CONTACT_SIGN);

    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard();
        finish();
    }
}
