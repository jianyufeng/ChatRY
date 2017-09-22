package com.sanbafule.sharelock.chatting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.SearchConversation;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewItemClickListener;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.SearchConversationResult;
import io.rong.message.TextMessage;

import static com.sanbafule.sharelock.R.id.conversion_name;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/27 15:06
 * cd : 三八妇乐
 * 描述：
 */
public class ConversationSearchListActivity extends AppCompatActivity {


    @Bind(R.id.search_input)
    ShareLockClearEditText searchInput;
    @Bind(R.id.input_layout)
    LinearLayout inputLayout;
    @Bind(R.id.cancel_search)
    TextView cancelSearch;
    @Bind(R.id.search_toolbar)
    Toolbar searchToolbar;
    @Bind(R.id.search_RecyclerView)
    ExpandRecyclerView searchRecyclerView;
    private int type;

    private Intent intent;
    private RecycleViewBaseAdapter adapter;
    private String content;
    private View head;
    private View foot;
    private TextView text;
    private ConversationSearchListActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search);
        ButterKnife.bind(this);
        setSupportActionBar(searchToolbar);
        mContext = this;
        intent = getIntent();
        type = intent.getIntExtra(SString.CONVERSATION_TYPE, -1);
        initSearchBefore();
    }

    //根据类型做些搜索前的初始化工作
    private void initSearchBefore() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (adapter.getItemCount() > 0) {
                    adapter.cleanAllDatas();
                }
                head.setVisibility(View.GONE);
                foot.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                if (content != null) {
                    text.setText(content);
                }
                searchConversationWhitType(content);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(manager);
        //需要添加的代码   创建适配器
        switch (type) {
            case 0:
                // 搜索私聊/群聊
                initSearchPrivateAndGroupBefore();
                break;
            case 1:
                // 搜索系统消息
                initSearchSystemBefore();
                break;
            default:
                break;
        }
        //设置适配器
        searchRecyclerView.setAdapter(adapter);
        //设置点击事件
        searchRecyclerView.addOnItemTouchListener(new RecycleViewItemClickListener(searchRecyclerView, onItemClick));
        //添加底部视图
        head = LayoutInflater.from(this).inflate(R.layout.item_search_conversation_heard, searchRecyclerView, false);
        foot = LayoutInflater.from(this).inflate(R.layout.item_search_conversation_foot, searchRecyclerView, false);
        text = (TextView) foot.findViewById(R.id.content);
        adapter.addHeaderView(head);
        adapter.addFooterView(foot);


    }

    /**
     * //创建系统消息的适配器
     */
    private void initSearchSystemBefore() {
        adapter = new RecycleViewBaseAdapter<SearchConversation, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_conversation);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, final int position) {
                SearchConversation item = getItem(position);
                Conversation conversation = item.getCnversation();
                if (item == null || conversation == null) {
                    return;
                }
                int count = item.getCount();
                String text = "%s条相关的聊天记录";
                holder.setText(R.id.conversion_body, String.format(text, count));
                holder.setText(R.id.conversion_name, "系统消息");
                holder.setImageResource(R.id.conversion_img, R.drawable.ic_launcher);
            }
        };
    }

    /**
     * //创建私聊/群聊的适配器
     */
    private void initSearchPrivateAndGroupBefore() {
        adapter = new RecycleViewBaseAdapter<SearchConversationResult, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_conversation);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, final int position) {
                SearchConversationResult item = getItem(position);
                Conversation conversation = item.getConversation();
                if (item == null || conversation == null) {
                    return;
                }
                int count = item.getMatchCount();
                // 私聊
                if (Conversation.ConversationType.PRIVATE == conversation.getConversationType()) {
                    String senderUserId = conversation.getSenderUserId();
                    String targetId = conversation.getTargetId();
                    String name;
                    if (senderUserId.equals(ShareLockManager.getInstance().getUserName())) {
                        // 发
                        name = targetId;
                    } else {
                        //  收
                        name = senderUserId;
                    }
                    ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(name);
                    if (info == null) {
                        ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
                            @Override
                            public void getContactInfo(ContactInfo contactInfo) {
                                holder.setImageResource(R.id.conversion_img, contactInfo.getU_header());
                                holder.setText(conversion_name, contactInfo.getName());
                            }
                        });
                    } else {
                        holder.setImageResource(R.id.conversion_img, info.getU_header());
                        holder.setText(conversion_name, info.getName());
                    }
                } else if (Conversation.ConversationType.GROUP == conversation.getConversationType()) {
                    String name = conversation.getTargetId();
                    GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(name);
                    if (groupInfo == null) {
                        holder.setText(conversion_name, groupInfo.getG_name());
                        holder.setImageResource(R.id.conversion_img, groupInfo.getG_header());
                    } else {
                        GroupInfoBiz.getGroupInfo(name, new GetGroupInfoListener() {
                            @Override
                            public void getGroupInfo(GroupInfo groupInfo) {
                                holder.setText(conversion_name, groupInfo.getG_name());
                                holder.setImageResource(R.id.conversion_img, groupInfo.getG_header());
                            }
                        });
                    }

                }

                String text = "%s条相关的聊天记录";
                holder.setText(R.id.conversion_body, String.format(text, count));
            }
        };
    }

    /**
     * 搜索私聊/群聊会话
     *
     * @param content
     */
    private void searchConversationWhitType(String content) {

        if (type == 0) {
            RongIMClient.getInstance().searchConversations(content, new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP}, new String[]{"RC:TxtMsg", "RC:ImgTextMsg", "RC:FileMsg"}, new RongIMClient.ResultCallback<List<SearchConversationResult>>() {
                @Override
                public void onSuccess(List<SearchConversationResult> searchConversationResults) {
                    if (searchConversationResults != null && !searchConversationResults.isEmpty()) {
                        adapter.fillList(searchConversationResults);
                        head.setVisibility(View.VISIBLE);
                        foot.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.i("onError" + errorCode);
                }
            });
        } else if (type == 1) {

            //1. 查询所有的会话
            RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                @Override
                public void onSuccess(List<Conversation> conversations) {
                    if (conversations == null || conversations.isEmpty()) return;
                    sift(conversations);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            }, Conversation.ConversationType.SYSTEM);
        }


    }

    /**
     * 筛选会话
     *
     * @param conversations
     */
    private void sift(List<Conversation> conversations) {
        ArrayList<SearchConversation> list = new ArrayList<>();
        final ArrayList<TextMessage> mList = new ArrayList<>();
        //2.遍历所有会话，取出 sendUserId不为 特殊字符串的
        for (int i = 0; i < conversations.size(); i++) {
            String userId = conversations.get(i).getSenderUserId();
            if (userId.equals("FirendAction") || userId.equals("GroupAction") || userId.equals("ManagerGroupAction")) {
                continue;
            }
            //3.查询某一个会话中所有的消息
            // 获取每个会话中的所有消息
            RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.SYSTEM, userId, 1000, new RongIMClient.ResultCallback<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messageList) {
                    if (messageList == null || messageList.isEmpty()) return;
                    for (int j = 0; j < messageList.size(); j++) {
                        Message message = messageList.get(j);
                        //4.找出所有消息中包含某一个特殊字符串的
                        if (message.getContent() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message.getContent();
                            String text = textMessage.getContent();
                            if (text.contains(content)) {
                                mList.add(textMessage);
                            }
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            SearchConversation searchConversation = new SearchConversation();
            searchConversation.setCnversation(conversations.get(i));
            searchConversation.setList(mList);
            searchConversation.setCount(mList.size());
            list.add(searchConversation);
            adapter.fillList(list);
        }
    }

    @OnClick({R.id.search_input, R.id.input_layout, R.id.cancel_search, R.id.search_RecyclerView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_input:
                break;
            case R.id.input_layout:
                break;
            case R.id.cancel_search:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.search_RecyclerView:
                break;
        }
    }

    /**
     * 检查输入的内容是否符要求
     *
     * @return
     */
    private boolean checkInputText() {

        content = searchInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showMessage(getString(R.string.search_empty), Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }


    //根据传入的类型进行不同的搜索
    private void searchByType() {
//        showDiglog();
//        switch (type) {
//            case 0:
//                searchFriend();
//                break;
//            case 1:
//                searchGroup();
//                break;
//            case 3:
//                searchLocalFriend();
//            default:
//        }
    }

    //item的点击事件
    private RecycleViewItemClickListener.OnItemClickListener onItemClick = new RecycleViewItemClickListener.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            Object item = adapter.getItem(position);
            if (item == null) {
                if (checkInputText()) {
                    searchByType();
                }
                return;
            }
            switch (type) {
                case 0:  //搜索出来的朋友点击事件
                    SearchConversationResult result = (SearchConversationResult) adapter.getItem(position);
                    if (result == null) {
                        return;
                    }
                    // 跳转页面
                    startActivity(new Intent(mContext, SearchConversationResultActivity.class).putExtra("MESSAGE_LIST_1", result).putExtra(SString.TYPE, type).putExtra("KEYWORD", content));
                    break;
                case 1: //搜索出来的群组点击事件
                    SearchConversation conversation = (SearchConversation) adapter.getItem(position);
                    if (conversation == null) {
                        return;
                    }
                    // 跳转页面
                    startActivity(new Intent(mContext, SearchConversationResultActivity.class).putExtra("MESSAGE_LIST_2", conversation).putExtra(SString.TYPE, type).putExtra("KEYWORD", content));
                    break;

                default:
                    break;
            }

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    /**
     * hide inputMethod
     * <p>
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = this.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }
}
