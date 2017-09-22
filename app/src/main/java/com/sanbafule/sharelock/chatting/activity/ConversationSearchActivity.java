package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseSearchActivity;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/27 11:10
 * cd : 三八妇乐
 * 描述：
 */
public class ConversationSearchActivity extends AppCompatActivity {


    @Bind(R.id.input_layout)
    LinearLayout inputLayout;
    @Bind(R.id.cancel_search)
    TextView cancelSearch;
    @Bind(R.id.search_toolbar)
    Toolbar searchToolbar;
    @Bind(R.id.private_conversation)
    LinearLayout privateConversation;
    @Bind(R.id.system_conversation)
    LinearLayout systemConversation;
    private int type;
    private Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversationsearch);
        ButterKnife.bind(this);
        setSupportActionBar(searchToolbar);
        privateConversation.setPressed(true);
    }


    @OnClick({R.id.input_layout, R.id.cancel_search, R.id.private_conversation, R.id.system_conversation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_layout:
                // 跳转界面
                intent = new Intent(this, ConversationSearchListActivity.class);
                intent.putExtra(SString.CONVERSATION_TYPE, type);
                startActivity(intent);
                break;
            case R.id.cancel_search:
                finish();
                break;
            case R.id.private_conversation:
                type = 0;
                break;
            case R.id.system_conversation:
                type = 1;
                break;
        }
    }
}
