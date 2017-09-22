package com.sanbafule.sharelock.UI.contact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.base.BaseSearchActivity;
import com.sanbafule.sharelock.comm.SString;

import butterknife.Bind;
import butterknife.OnClick;


public class SearchFriendAndGroupActivity extends BaseActivity {


    @Bind(R.id.friend)
    TextView friend;
    @Bind(R.id.group)
    TextView group;
    @Bind(R.id.line1)
    View line1;
    @Bind(R.id.id_add_friend)
    ImageView idAddFriend;
    @Bind(R.id.id_add_group)
    ImageView idAddGroup;
    @Bind(R.id.top_layout)
    LinearLayout topLayout;
    @Bind(R.id.content_input_layout)
    LinearLayout contentInputLayout;
    @Bind(R.id.leida)
    LinearLayout leida;
    @Bind(R.id.mianduimian)
    LinearLayout mianduimian;
    @Bind(R.id.saoyisao)
    LinearLayout saoyisao;
    private int type;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.addfriend, -1, -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_friend_and_group;
    }


    @OnClick({R.id.id_add_friend, R.id.id_add_group, R.id.input_layout, R.id.leida, R.id.mianduimian, R.id.saoyisao, R.id.friend, R.id.group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_add_friend:
                break;
            case R.id.id_add_group:
                break;
            case R.id.input_layout:
                intent = new Intent(this, BaseSearchActivity.class);
                intent.putExtra(SString.TYPE, type);
                intent.putExtra(SString.SEARCH_HINT,type==0?getString(R.string.search_friend_hint):getString(R.string.search_group_hint));
                startActivity(intent);
                break;
            case R.id.leida:
                // 跳转到雷达界面
                break;
            case R.id.mianduimian:
                // 跳转到面对面建群
                break;
            case R.id.saoyisao:
                // 跳转到扫一扫
                break;
            case R.id.friend:
                type = 0;
                showView(type);
                break;
            case R.id.group:
                type = 1;
                showView(type);
                break;
        }
    }

    private void showView(int type) {
        if(type==0){
            TranslateAnimation animation=new TranslateAnimation(line1.getWidth(),0,0,0);
            animation.setFillAfter(true);
            animation.setDuration(500);
            line1.startAnimation(animation);
        }else {
            TranslateAnimation animation=new TranslateAnimation(0,line1.getWidth(),0,0);
            animation.setFillAfter(true);
            animation.setDuration(500);
            line1.startAnimation(animation);
        }
    }


}
