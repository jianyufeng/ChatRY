package com.sanbafule.sharelock.chatting.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactBiz;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.ContactsCache;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.modle.Contact;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

import static com.sanbafule.sharelock.R.id.imageView;


/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/5 10:23
 * cd : 三八妇乐
 * 描述：仿微信的转发信息的页面
 */
public class TranspondMessageActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.select_listview)
    ExpandRecyclerView selectListview;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.editText)
    EditText editText;
    @Bind(R.id.transpondList)
    ExpandRecyclerView transpondList;
    //联系人列表的头部的文字
    private TextView textView;
    // 联系人列表的头部
    private View head;
    private RecycleViewBaseAdapter adapter;
    // 多选/单选
    private boolean select;
    private ContactInfo Info;
    private RecycleViewBaseAdapter contactInfoAdapter;
    // 所有选中的消息
    private ArrayList<Message> messageList;
    // 所有选中的联系人
    private List<String> contactNameList = new ArrayList<>();
    // 所有的联系人
    private List<Contact> list;
    private Intent intent;
    // 要转发的消息
    private Message message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageList = getIntent().getParcelableArrayListExtra("SelectMessageList");
        message = getIntent().getParcelableExtra("MESSAGE");
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "选择", "多选", -1, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        transpondList.setLayoutManager(manager);
        transpondList.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));
        head = LayoutInflater.from(this).inflate(R.layout.top_transpond_message, transpondList, false);
        textView = (TextView) head.findViewById(R.id.new_conversation);
        textView.setOnClickListener(this);
        adapter = new RecycleViewBaseAdapter<Contact, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_transpond_message_contact);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, final int position) {
                final Contact item = getItem(position);
                if (item == null) return;
                String username = item.getU_username();
                Info = ContactInfoSqlManager.getContactInfoFormuserName(username);
                if (Info != null) {
                    holder.setImageResource(R.id.image, Info.getU_header());
                    holder.setText(R.id.name, Info.getName());
                } else {
                    ContactInfoBiz.getContactInfo(username, new GetContactInfoListener() {
                        @Override
                        public void getContactInfo(ContactInfo contactInfo) {
                            Info = contactInfo;
                            holder.setImageResource(R.id.image, Info.getU_header());
                            holder.setText(R.id.name, Info.getName());
                        }
                    });
                }

                if (select) {
                    holder.getView(R.id.check).setVisibility(View.VISIBLE);
                    ((CheckBox) holder.getView(R.id.check)).setChecked(false);
                    ((CheckBox) holder.getView(R.id.check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                            showCheck(isChecked, getItem(position).getU_username());
                        }
                    });
                } else {
                    holder.getView(R.id.check).setVisibility(View.GONE);
                }
            }
        };
//        //设置点击事件
        transpondList.setAdapter(adapter);
        adapter.addHeaderView(head);
        adapter.setItemClickAndLongClick(click);
        refreshData();
        registerReceiver(new String[]{ReceiveAction.ACTION_CONTACTS_CHANGE});

    }

    /**
     * 显示选中的联系人列表
     *
     * @param isChecked
     * @param userName
     */
    private void showCheck(boolean isChecked, String userName) {
        if (isChecked) {
            contactNameList.add(userName);
        } else {
            contactNameList.remove(userName);
        }

        if (!contactNameList.isEmpty()) {
            getToolBarHelper().getRightTextView().setText("发送" + "(" + contactNameList.size() + ")");
            getToolBarHelper().getRightTextView().setBackgroundColor(Color.GREEN);
        } else {
            getToolBarHelper().getRightTextView().setText("单选");
            getToolBarHelper().getRightTextView().setBackgroundColor(Color.GREEN);
        }
        showSelectContactInfo();
    }

    /**
     * 显示选中的ContactInfo
     */
    private void showSelectContactInfo() {

        if (contactNameList.isEmpty()) {
            selectListview.setVisibility(View.GONE);
        } else {
            selectListview.setVisibility(View.VISIBLE);
            if (contactInfoAdapter == null) {
                LinearLayoutManager manager = new LinearLayoutManager(TranspondMessageActivity.this);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                selectListview.setLayoutManager(manager);
                contactInfoAdapter = new RecycleViewBaseAdapter<String, RecycleViewBaseHolder>(this) {
                    @Override
                    public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                        return new RecycleViewBaseHolder(parent, R.layout.item_select_contactinfo);
                    }

                    @Override
                    public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                        final ContactInfo Info = ContactInfoSqlManager.getContactInfoFormuserName(getItem(position));
                        if (Info != null) {
                            holder.setImageResource(imageView, Info.getU_header());
                        } else {
                            ContactInfoBiz.getContactInfo(getItem(position), new GetContactInfoListener() {
                                @Override
                                public void getContactInfo(ContactInfo contactInfo) {
                                    holder.setImageResource(imageView, contactInfo.getU_header());
                                }
                            });
                        }

                    }
                };
                selectListview.setAdapter(contactInfoAdapter);
            }
            contactInfoAdapter.fillList(contactNameList);

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 获取联系人列表
        ContactBiz.getContactList();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transpond_message;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                // 多选
                select = !select;
                if (select) {
                    getToolBarHelper().getRightTextView().setText("单选");
                    getToolBarHelper().getRightTextView().setBackgroundColor(Color.GREEN);
                } else {
                    getToolBarHelper().getRightTextView().setText("多选");
                    getToolBarHelper().getRightTextView().setBackgroundColor(Color.GREEN);
                    if (!contactNameList.isEmpty()) {
                        // 群发
                        sendMessageWithGroup();
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.new_conversation:
                // 创建新的会话
        }
    }


    /**
     * 群发消息
     */
    public void sendMessageWithGroup() {

        if (contactNameList.isEmpty()) return;
        // 单个消息群发
        if (message != null) {
            DialogUtils.showGroupTranspondMessageDialgo(TranspondMessageActivity.this, message, contactNameList, new DialogUtils.DialogInterface() {
                @Override
                public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                    EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                    String text = editText.getText().toString().trim();
                    for (int i = 0; i < contactNameList.size(); i++) {
                        MessageContent content = message.getContent();
                        String name = contactNameList.get(i);
                        // 转发文字消息
                        if (content instanceof TextMessage) {
                            SChattingHelp.transpondTextMessage(name, (TextMessage) content);
                            //转发图片消息
                        } else if (content instanceof ImageMessage) {
                            SChattingHelp.transpondImageMessage(name, (ImageMessage) content);
                            // 转发文件消息
                        } else if (content instanceof FileMessage) {
                            if (((FileMessage) content).getType().equals(FileMessageType.GIF.toString())) {
                                SChattingHelp.transpondGifMessage(name, (FileMessage) content);
                            } else if (((FileMessage) content).getType().equals(FileMessageType.VIDEO.toString())) {
                                SChattingHelp.transpondVideoMessage(name, (FileMessage) content);
                            } else {

                            }
                        }
                        if (MyString.hasData(text)) {
                            SChattingHelp.transpondTextMessage(name, text);
                        }
                    }
                }

                @Override
                public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

                }
            });
        } else if (messageList != null && !messageList.isEmpty()) {

            // 1. 拿到所有的消息

            DialogUtils.showCustomDialog(TranspondMessageActivity.this, contactNameList, messageList, new DialogUtils.DialogInterface() {
                @Override
                public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                    //3. 循环向每个人发送消息
                    EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                    String text = editText.getText().toString().trim();
                    for (int i = 0; i < contactNameList.size(); i++) {
                        String name = contactNameList.get(i);
                        for (int j = 0; j < messageList.size(); j++) {
                            Message message = messageList.get(i);
                            MessageContent content = message.getContent();
                            if (content instanceof ImageMessage) {
                                //转发图片消息
                                SChattingHelp.transpondImageMessage(name, (ImageMessage) content);
                                if (MyString.hasData(text)) {
                                    SChattingHelp.transpondTextMessage(name, text);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

                }
            });

        }


    }

    //item 的点击事件
    private RecyclerViewClick.Click click = new RecyclerViewClick.Click() {
        @Override
        public void onItemClick(View view, int position) {
            Contact item = (Contact) adapter.getItem(position);
            //弹出对话框
            showDialog(item.getU_username());
            // 转发消息
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };


    /**
     * 显示对话框
     *
     * @param userName
     */
    private void showDialog(final String userName) {

        final ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(userName);
        if (contactInfo != null) {
            if (message != null) {
                // 转发单个消息
                DialogUtils.showTranspondMessageDialog(TranspondMessageActivity.this, message, contactInfo, new DialogUtils.DialogInterface() {
                    @Override
                    public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                        EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                        String text = editText.getText().toString().trim();
                        MessageContent content = message.getContent();
                        //文字转发
                        if (content instanceof TextMessage) {
                            SChattingHelp.transpondTextMessage(userName, (TextMessage) content);
                            // 图片转发
                        } else if (content instanceof ImageMessage) {
                            SChattingHelp.transpondImageMessage(userName, (ImageMessage) content);
                            // 文件转发
                        } else if (content instanceof FileMessage) {
                            if (((FileMessage) content).getType().equals(FileMessageType.GIF.toString())) {
                                // Gif 消息转发
                                SChattingHelp.transpondGifMessage(userName, (FileMessage) content);
                            } else if (((FileMessage) content).getType().equals(FileMessageType.VIDEO.toString())) {
                                // 小视屏转发
                                SChattingHelp.transpondVideoMessage(userName, (FileMessage) content);
                            } else {

                            }

                        }
                        if (MyString.hasData(text)) {
                            SChattingHelp.transpondTextMessage(userName, text);
                        }
                    }

                    @Override
                    public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

                    }
                });
            } else {
                DialogUtils.showCustomDialog(TranspondMessageActivity.this, contactInfo, messageList, new DialogUtils.DialogInterface() {
                    @Override
                    public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                        EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                        String text = editText.getText().toString().trim();
                        if (text == null) text = "";
                        ToastUtil.showMessage(editText.getText().toString() + "");
                        for (int i = 0; i < messageList.size(); i++) {
                            Message message = messageList.get(i);
                            MessageContent content = message.getContent();
                            if (content instanceof ImageMessage) {
                                //转发图片消息
                                SChattingHelp.transpondImageMessage(contactInfo.getU_username(), (ImageMessage) content);
                                SChattingHelp.transpondTextMessage(contactInfo.getU_username(), text);
                            } else if (content instanceof TextMessage) {
                                //转发图片消息
                                SChattingHelp.transpondTextMessage(userName, (TextMessage) content);
                            }
                        }
                    }

                    @Override
                    public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

                    }
                });
            }

        } else {
            ContactInfoBiz.getContactInfo(userName, new GetContactInfoListener() {
                @Override
                public void getContactInfo(final ContactInfo contactInfo) {
                    DialogUtils.showCustomDialog(TranspondMessageActivity.this, contactInfo, messageList, new DialogUtils.DialogInterface() {
                        @Override
                        public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                            EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                            String text = editText.getText().toString().trim();
                            if (text == null) text = "";
                            ToastUtil.showMessage(editText.getText().toString() + "");
                            for (int i = 0; i < messageList.size(); i++) {
                                Message message = messageList.get(i);
                                MessageContent content = message.getContent();
                                if (content instanceof ImageMessage) {
                                    //转发图片消息
                                    SChattingHelp.transpondTextMessage(contactInfo.getU_username(), text);
                                    SChattingHelp.transpondImageMessage(contactInfo.getU_username(), (ImageMessage) content);
                                }
                            }
                        }

                        @Override
                        public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

                        }
                    });
                }
            });
        }

    }

    private void refreshData() {
        list = ContactsCache.getInstance().getContacts();
        if (adapter != null && list != null) {
            adapter.fillList(list);
        }
    }


    // 广播接收器

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_CONTACTS_CHANGE.equals(intent.getAction())) {
            refreshData();
        }
    }



}
