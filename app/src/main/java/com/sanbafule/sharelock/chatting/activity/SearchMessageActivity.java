package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.SimpleCommonUtils;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;


/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/14 14:52
 * cd : 三八妇乐
 * 描述：
 */
public class SearchMessageActivity extends BaseActivity {


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
    @Bind(R.id.view_emp)
    View viewEmp;
    @Bind(R.id.empty_view)
    TextView emptyView;
    private String name;
    private Intent intent;
    private RecycleViewBaseAdapter adapter;
    private String content;
    private String format = "没有找到%s相关结果";
    private List<Message> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        name = intent.getStringExtra(SString.NAME);
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
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                searchLocalMessage();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(manager);
        //设置分割线
        searchRecyclerView.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));
        searchRecyclerView.setEmptyView(viewEmp);
        adapter = new RecycleViewBaseAdapter<Message, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_search_message);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                final Message item = getItem(position);
                if (item == null) return;
                final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(item.getTargetId());
                if (info == null) {
                    ContactInfoBiz.getContactInfo(item.getTargetId(), new GetContactInfoListener() {
                        @Override
                        public void getContactInfo(ContactInfo info) {
                            holder.setText(R.id.name, info.getName());
                            holder.setImageResource(R.id.icon, ShareLockManager.getImgUrl(info.getU_header()));
                            if (item.getContent() instanceof TextMessage) {
                                SimpleCommonUtils.spannableEmoticonFilter
                                        ((TextView) holder.getView(R.id.content), ((TextMessage) item.getContent()).getContent());
                            }
                        }
                    });

                } else {
                    holder.setText(R.id.name, info.getName());
                    holder.setImageResource(R.id.icon, ShareLockManager.getImgUrl(info.getU_header()));
                    if (item.getContent() instanceof TextMessage) {
                        SimpleCommonUtils.spannableEmoticonFilter
                                ((TextView) holder.getView(R.id.content), ((TextMessage) item.getContent()).getContent());
                    }


                }
            }

        };
        //设置适配器
        searchRecyclerView.setAdapter(adapter);
        //设置点击事件
        adapter.setItemClickAndLongClick(new RecyclerViewClick.Click() {
            @Override
            public void onItemClick(View view, int position) {
                Message item = (Message) adapter.getItem(position);
                intent = new Intent(SearchMessageActivity.this, ShowBigTextActivity.class);
                intent.putExtra(SString.CONTENT, ((TextMessage) item.getContent()).getContent());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_message;
    }


    /**
     * 搜索本地消息
     */
    private void searchLocalMessage() {
        RongIMClient.getInstance().searchMessages(Conversation.ConversationType.PRIVATE, name, content, 30, 0, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if (messageList.isEmpty()) {
                    emptyView.setText(String.format(format, content));
                    return;
                }

                adapter.fillList(messageList);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                emptyView.setText(String.format(format, content));
            }
        });
    }

    @Override
    public boolean isNeedToolBar() {
        return false;
    }


    @OnClick(R.id.cancel_search)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_search:
                hideSoftKeyboard();
                finish();
                break;
        }

    }
}
