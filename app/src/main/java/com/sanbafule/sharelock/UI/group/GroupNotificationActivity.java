package com.sanbafule.sharelock.UI.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;


/**
 * 作者:Created by 简玉锋 on 2017/1/5 17:33
 * 邮箱: jianyufeng@38.hn
 */

public class GroupNotificationActivity extends BaseActivity implements View.OnClickListener {
    //群组通知的容器
    private ExpandRecyclerView groupNoticceContainer;
    //空view
    private View emptyView;
    //适配器
    private RecycleViewBaseAdapter<Message, RecycleViewBaseHolder> adapter;

    //群组个数
    private String targetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetId = getIntent().getStringExtra(SString.TARGETNAME);
        if (TextUtils.isEmpty(targetId)) {
            finish();
            return;
        }
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.group_notice_title), getString(R.string.clean), -1, this);
        initData();
        initView();
//        asynData();
        logicControlView();
//        refreshData();
    }

    private void initView() {
        groupNoticceContainer = (ExpandRecyclerView) findViewById(R.id.recycle_view_id);
        emptyView = findViewById(R.id.id_empty_view);
    }

    private void initData() {
        RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.SYSTEM,
                targetId, 20, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
                    @Override
                    public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                        if (messages != null && messages.size() > 0) {
                            adapter.fillList(messages);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showMessage(getString(R.string.group_details_get_err));
                    }
                }
        );
    }

    public void logicControlView() {
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        groupNoticceContainer.setLayoutManager(manager);
        //设置空View
        groupNoticceContainer.setEmptyView(emptyView);
        //设置分割线
        groupNoticceContainer.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));    //创建群组列表的适配器
        //创建群组列表的适配器
        adapter = new RecycleViewBaseAdapter<Message, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_group_list);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                Message item = getItem(position);
                if (item == null) {
                    return;
                }

                MessageContent content = item.getContent();
                if (content instanceof TextMessage) {
                    TextMessage message = (TextMessage) content;
                    String json = message.getContent();
                    try {
                        JSONObject object = new JSONObject(json);
                        String type = object.getString(SString.OPERATION);
                        if (!TextUtils.isEmpty(type)) {
                            //解散群组
                            if (type.equals(SString.DISMISS_GROUP)) {
                                final String groupId = object.getString("groupId");
                                String operator = object.getString("operator");

                                ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(operator);
                                String neckName = null;
                                String groupName = null;
                                if (contactInfo != null) {
                                    neckName = contactInfo.getU_nickname();
                                    if (TextUtils.isEmpty(neckName)) {
                                        neckName = operator;
                                    }
//                                    contactInfo.getU_nickname():contactInfo.getName()
//                                    setContantInfo(holder, getString(R.string.group_dis_tip,
//                                            , ""), );
                                } else {
                                    ContactInfoBiz.getContactInfo(operator, new GetContactInfoListener() {
                                        @Override
                                        public void getContactInfo(ContactInfo contactInfo) {
                                        }
                                    });
                                }
                                GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
                                if (groupInfo != null) {
                                    groupName = groupInfo.getG_name();
                                    setContantInfo(holder, getString(R.string.group_dis_tip, neckName, groupName), groupInfo);
                                } else {
                                    final String finalNeckName = neckName;
                                    GroupInfoBiz.getGroupInfo(groupId, new GetGroupInfoListener() {
                                        @Override
                                        public void getGroupInfo(GroupInfo groupInfo) {
                                            setContantInfo(holder, getString(R.string.group_dis_tip, finalNeckName, groupInfo.getG_name()), groupInfo);
                                        }
                                    });
                                }


                            } else if (type.equals(SString.KICK_MESSAGE)) {
                                //踢出群组
                                String groupId = object.getString("groupId");
                                GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
                                if (groupInfo != null) {
                                    setContantInfo(holder, getString(R.string.group_member_kick, groupInfo.getG_name()), groupInfo);
                                } else {
                                    GroupInfoBiz.getGroupInfo(groupId, new GetGroupInfoListener() {
                                        @Override
                                        public void getGroupInfo(GroupInfo groupInfo) {
                                            setContantInfo(holder, getString(R.string.group_member_kick, groupInfo.getG_name()), groupInfo);
                                        }
                                    });
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            private void setContantInfo(RecycleViewBaseHolder holder, String msg, GroupInfo groupInfo) {
                if (!TextUtils.isEmpty(msg)) {
                    holder.setText(R.id.group_name, msg);
                }
                String url = groupInfo.getG_header();
                if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                    url = Url.CORE_IP + url;
                }
                holder.setImageResource(R.id.group_icon, url);
            }
        };

        //设置适配器
        groupNoticceContainer.setAdapter(adapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_notice;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                cleaAllNotice();
                break;
        }
    }

    private void cleaAllNotice() {
        //应该有确定
        GroupService.deleteMessage(Conversation.ConversationType.SYSTEM, targetId);
        finish();
    }
}
