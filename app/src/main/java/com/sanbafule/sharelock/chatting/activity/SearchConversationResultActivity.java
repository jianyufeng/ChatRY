package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.SearchConversation;
import com.sanbafule.sharelock.util.StringUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.SearchConversationResult;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/28 11:37
 * cd : 三八妇乐
 * 描述：
 */
public class SearchConversationResultActivity extends BaseActivity implements View.OnClickListener{


    @Bind(R.id.search_message_RecyclerView)
    ExpandRecyclerView searchMessageRecyclerView;

    private SearchConversationResult result;
    private SearchConversation searchConversation;
    private Intent intent;
    private int type;
    private RecycleViewBaseAdapter adapter;
    private String keyword;
    private Conversation conversation;
    private SearchConversationResultActivity mContext;
    private View head;
    private TextView headText;
    private  String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,"消息列表",null,-1,this);
        intent = getIntent();
        type = intent.getIntExtra(SString.TYPE, -1);
        keyword = intent.getStringExtra("KEYWORD");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchMessageRecyclerView.setLayoutManager(manager);
//        searchMessageRecyclerView.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));
        switch (type) {
            case 0:
                result = intent.getParcelableExtra("MESSAGE_LIST_1");
                conversation=result.getConversation();
                makePrivateAdapter();
                initPrivateData();
                break;
            case 1:
                searchConversation = intent.getParcelableExtra("MESSAGE_LIST_2");
                conversation=searchConversation.getCnversation();
                makeSystemAdapter();
                initSystemData();
                break;
            default:
                break;
        }
         text="共%s条与%s相关的聊天记录";
        head= LayoutInflater.from(this).inflate(R.layout.item_search_conversation_heard,searchMessageRecyclerView,false);
        //设置点击事件
        searchMessageRecyclerView.addOnItemTouchListener(new RecycleViewItemClickListener(searchMessageRecyclerView, onItemClick));
        searchMessageRecyclerView.setAdapter(adapter);
        headText = (TextView) head.findViewById(R.id.head_text);
        adapter.addHeaderView(head);
    }


    private void initPrivateData() {
        if (result == null || !MyString.hasData(keyword)) return;
        String name;
        if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
            name = SChattingHelp.getConversation(conversation);
        } else {
            name = conversation.getTargetId();
        }

        RongIMClient.getInstance().searchMessages(conversation.getConversationType(), name, keyword, 0, 0, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if (messageList != null && !messageList.isEmpty()) {
                    adapter.fillList(messageList);
                    head.setVisibility(View.VISIBLE);headText.setText(String.format(text,messageList.size(),keyword));
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

    }

    private void initSystemData() {
        if (searchConversation == null) return;
        ArrayList<TextMessage> list = searchConversation.getList();
        if (list != null && !list.isEmpty()) {
            adapter.fillList(list);
        }
    }


    private void makeSystemAdapter() {
        adapter = new RecycleViewBaseAdapter<TextMessage, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_conversation);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                TextMessage message = getItem(position);
            }


        };

    }

    private void makePrivateAdapter() {
        adapter = new RecycleViewBaseAdapter<Message, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_conversation);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                Message message = getItem(position);
                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String name;
                    if(conversation.getConversationType()== Conversation.ConversationType.PRIVATE){
                        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                            name = message.getTargetId();
                        } else {
                            name = message.getSenderUserId();
                        }
                        //查找联系人
                        // 设置信息
                        ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(name);
                       if(info==null){
                           ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
                               @Override
                               public void getContactInfo(ContactInfo contactInfo) {
                                   holder.setText(R.id.conversion_name,contactInfo.getName());
                                   holder.setImageResource(R.id.conversion_img,contactInfo.getU_header());
                               }
                           });
                       }else {
                           holder.setText(R.id.conversion_name,info.getName());
                           holder.setImageResource(R.id.conversion_img,info.getU_header());
                       }
                         holder.getView(R.id.conversion_groupMember).setVisibility(View.GONE);
                    }else  if(conversation.getConversationType()== Conversation.ConversationType.GROUP){
                        name=message.getTargetId();
                        String sendUserId=message.getSenderUserId();
                        if(sendUserId.equals(ShareLockManager.getInstance().getUserName())){
                            holder.getView(R.id.conversion_groupMember).setVisibility(View.GONE);
                            holder.setText(R.id.conversion_groupMember,"我:");
                        }else {
                            ContactInfo info=ContactInfoSqlManager.getContactInfoFormuserName(sendUserId);
                            if(info==null){
                                ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
                                    @Override
                                    public void getContactInfo(ContactInfo contactInfo) {
                                        holder.getView(R.id.conversion_groupMember).setVisibility(View.GONE);
                                        holder.setText(R.id.conversion_groupMember,contactInfo.getName());
                                    }
                                });
                            }else {
                                holder.getView(R.id.conversion_groupMember).setVisibility(View.GONE);
                                holder.setText(R.id.conversion_groupMember,info.getName());
                            }
                        }

                        GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(name);
                        if(groupInfo==null){
                            GroupInfoBiz.getGroupInfo(name, new GetGroupInfoListener() {
                                @Override
                                public void getGroupInfo(GroupInfo groupInfo) {
                                    holder.setText(R.id.conversion_name,groupInfo.getG_name());
                                    holder.setImageResource(R.id.conversion_img,groupInfo.getG_header());
                                }
                            });
                        }
                    }
                    holder.setText(R.id.conversion_body, StringUtil.matcherSearchTitle(textMessage.getContent(),keyword));
                }
            }


        };
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_searchconversationresult;
    }


    //item的点击事件
    private RecycleViewItemClickListener.OnItemClickListener onItemClick = new RecycleViewItemClickListener.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            Object item = adapter.getItem(position);
            if (item == null) {
                return;
            }
            switch (type) {
                case 0:  //搜索出来的朋友点击事件
                    Message result = (Message) adapter.getItem(position);
                    if (result == null) {
                        return;
                    }
                    // 跳转页面
                    startActivity(new Intent(mContext, GroupChattingActivity.class));
                    break;
                case 1: //搜索出来的群组点击事件
//                    SearchConversation conversation = (SearchConversation) adapter.getItem(position);
//                    if (conversation == null) {
//                        return;
//                    }
//                    // 跳转页面
//                    startActivity(new Intent(mContext, SearchConversationResultActivity.class).putExtra("MESSAGE_LIST_2", conversation).putExtra(SString.TYPE, type).putExtra("KEYWORD", content));
                    break;

                default:
                    break;
            }

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left:
                hideSoftKeyboard();
                finish();
            break;
        }
    }
}
