package com.sanbafule.sharelock.UI.contact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.adapter.ContactSelectAdapter;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选择联系人界面 邀请成员加入群组
 */
public class ContactListSelectActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.contact_list)
    WrapRecyclerView contactList;
    @Bind(R.id.letter_view)
    LetterView letterView;
    private LinearLayoutManager layoutManager;
    private ContactSelectAdapter adapter;
    private List<Contact> contacts;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshData();
        }
    };
    private String groupId; //群组id

    private void refreshData() {
        contacts = ContactsCache.getInstance().getContacts();
        if (adapter != null) {
            adapter.refreshData(contacts);
        }

        closeCoverLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        //设置ActionBar
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.select_friends_title, R.string.select_friends_confirm, -1, this);
        //左边的快速定位栏
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
        groupId = getIntent().getStringExtra(SString.GROUP_ID);
        if (TextUtils.isEmpty(groupId)){
            finish();
            return;
        }
        //加载时的遮罩
        showCoverLayout();
        initDataAndView();
        registerReceiver(new String[]{ReceiveAction.ACTION_CONTACTS_CHANGE});
    }

    private void initDataAndView() {
        layoutManager = new LinearLayoutManager(this);
        contactList.setLayoutManager(layoutManager);
        View searchLayout = LayoutInflater.from(this).inflate(R.layout.base_search_layout, contactList, false);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showMessage("fdssds");
            }
        });
        contactList.addHeaderView(searchLayout);
        adapter = new ContactSelectAdapter(this, contacts,groupId);
        contactList.setAdapter(adapter);
        handler.sendEmptyMessage(1);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_list;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 下载联系人
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpGetBiz(Url.GET_CONTACT_LIST, map, new HttpInterface() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSucceed(String s) {


                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONObject r = SString.getResult(object);
                            JSONArray friendData = r.getJSONArray("data");
                            Gson gson = new Gson();
                            List<Contact> contacts = gson.fromJson(friendData.toString(), new TypeToken<List<Contact>>() {
                            }.getType());
                            ContactSqlManager.insertContacts(contacts);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.left:
                finish();
                break;
            case R.id.right_text:
                ArrayList<String> selectedContacts = adapter.getSelectedContacts();
                if (selectedContacts == null || selectedContacts.isEmpty()) {
                    return;
                }
                //使用 SString.GROUP_ADD_FLAG 做标记传参
                setResult(RESULT_OK, new Intent().putStringArrayListExtra(SString.GROUP_ADD_FLAG, selectedContacts));
                finish();
        }

    }


    // 广播接收器

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_CONTACTS_CHANGE.equals(intent.getAction())) {
            handler.sendEmptyMessage(1);
        }
    }

}
