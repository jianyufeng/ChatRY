package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.comm.SString;

import butterknife.Bind;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/14 17:02
 * cd : 三八妇乐
 * 描述：
 */
public class ShowBigTextActivity extends AppCompatActivity {


    private String content;

    private Intent intent;
    private TextView bigText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bigtext);
        bigText = (TextView) findViewById(R.id.big_text);
        intent = getIntent();
        content = intent.getStringExtra(SString.CONTENT);
        bigText.setText(content);
    }


}
