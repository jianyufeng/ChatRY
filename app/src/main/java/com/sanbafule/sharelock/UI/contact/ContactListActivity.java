package com.sanbafule.sharelock.UI.contact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.base.BaseSearchActivity;
import com.sanbafule.sharelock.UI.group.GroupListActivity;
import com.sanbafule.sharelock.adapter.ContactAdapter;
import com.sanbafule.sharelock.business.ContactBiz;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.ContactsCache;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.Contact;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.LetterView;
import com.sanbafule.sharelock.view.WrapRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;

/**
 *
 */
public class ContactListActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.contact_list)
    WrapRecyclerView contactList;
    @Bind(R.id.letter_view)
    LetterView letterView;
    private LinearLayoutManager layoutManager;
    private ContactAdapter adapter;
    private List<Contact> contacts;
    private LayoutInflater inflater;
    private View contact_head_layout,contact_foot_layout;
    private TextView contact_Number_tv;


    private LinearLayout searchLayout, newFriendLayout, groupLayout, customServicelayout;
    private Intent intent;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshData();
        }
    };

    private void refreshData() {
        contacts= ContactsCache.getInstance().getContacts();
        if (adapter != null) {
            adapter.refreshData(contacts);
        }
        if(contact_Number_tv!=null){
            if(contacts!=null){
                contact_Number_tv.setText("共有"+contacts.size()+"位好友");
            }else {
                contact_Number_tv.setVisibility(View.GONE);
            }

        }
        closeCoverLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.friend, R.string.addfriend, -1, this);

        letterView.setCharacterListener(new LetterView.CharacterClickListener() {
            @Override
            public void clickCharacter(String character) {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(character), 0);
            }

            @Override
            public void clickArrow() {

                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
        showCoverLayout();
        initDataAndView();
        registerReceiver(new String[]{ReceiveAction.ACTION_CONTACTS_CHANGE});
    }

    private void initDataAndView() {
        layoutManager = new LinearLayoutManager(this);
        contactList.setLayoutManager(layoutManager);
        contact_head_layout= inflater.inflate(R.layout.contact_head_layout,contactList,false);
        searchLayout = (LinearLayout) contact_head_layout.findViewById(R.id.contact_layout_search);
        searchLayout.setOnClickListener(this);
        newFriendLayout = (LinearLayout) contact_head_layout.findViewById(R.id.contact_heard_newfriend);
        newFriendLayout.setOnClickListener(this);
        groupLayout = (LinearLayout) contact_head_layout.findViewById(R.id.contact_heard_group);
        groupLayout.setOnClickListener(this);
        customServicelayout = (LinearLayout) contact_head_layout.findViewById(R.id.contact_heard_custom_service);
        customServicelayout.setOnClickListener(this);
        contact_foot_layout=inflater.inflate(R.layout.contact_foot_item, contactList,false);
        contactList.addHeaderView(contact_head_layout);
        contactList.addFootView(contact_foot_layout);
        contact_Number_tv= (TextView) contact_foot_layout.findViewById(R.id.number);
        adapter = new ContactAdapter(this, contacts,contactList);
        contactList.setAdapter(adapter);


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_list;
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(1);
        // 获取联系人列表
        ContactBiz.getContactList();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.left:
                finish();
                break;
            case R.id.contact_layout_search:
                // 本地搜素联系人
                intent=new Intent(this, BaseSearchActivity.class);
                intent.putExtra(SString.TYPE,3);
                intent.putExtra(SString.SEARCH_HINT,getString(R.string.search_friend_hint));
                startActivity(intent);
                break;
            case R.id.contact_heard_newfriend:
                // 新朋友
                intent = new Intent(this, NewFriendListActivity.class);
                startActivity(intent);
                break;
            case R.id.contact_heard_group:
                //群组
                ToastUtil.showMessage("群组");
                startActivity(new Intent(this, GroupListActivity.class));
                break;
            case R.id.contact_heard_custom_service:
                //客服
                ToastUtil.showMessage("客服");
                break;
            case R.id.right_text:
                intent = new Intent(this, SearchFriendAndGroupActivity.class);
                startActivity(intent);

        }

    }



    // 广播接收器

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if(ReceiveAction.ACTION_CONTACTS_CHANGE.equals(intent.getAction())){
            handler.sendEmptyMessage(1);
        }
    }

}
