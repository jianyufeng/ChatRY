package com.sanbafule.sharelock;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.chatting.Interface.GetLatestMessagesCallBack;
import com.sanbafule.sharelock.chatting.Interface.SendMessageCallBack;
import com.sanbafule.sharelock.chatting.activity.PrivateChattingInfoActivity;
import com.sanbafule.sharelock.chatting.adapter.ChattingAdapter;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.AppBean;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.chatting.view.AudioRecorderButton;
import com.sanbafule.sharelock.chatting.view.SimpleAppsGridView;
import com.sanbafule.sharelock.comm.help.CurrencyHelp;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.SimpleCommonUtils;
import com.sanbafule.sharelock.view.MyKeyBoard;
import com.sanbafule.sharelock.view.header.MyHeader;
import com.sanbafule.sharelock.view.header.PtrDefaultHandler;
import com.sanbafule.sharelock.view.header.PtrFrameLayout;
import com.sanbafule.sharelock.view.header.PtrHandler;
import com.sj.emoji.EmojiBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import me.iwf.photopicker.PhotoPickUtils;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

public class TestActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener,
        AutoHeightLayout.OnMaxParentHeightChangeListener,
        RongIMClient.OnReceiveMessageListener,
        View.OnClickListener, SendMessageCallBack {

    private static final String TAG = "TestActivity";
    @Bind(R.id.lv_chat)
    ListView lvChat;
    @Bind(R.id.ek_bar)
    MyKeyBoard ekBar;
    @Bind(R.id.rotate_header_list_view_frame)
    PtrFrameLayout ptrFrameLayout;


    public static int progress;
    private ChattingAdapter mChattingAdapter;
    private SimpleAppsGridView gridView;
    private List<ShareLockMessage> list;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private Intent intent;
    private String targetId;
    // 最后一条消息的Id;
    private int lastMessageId;

    public static TestActivity mInstance;
    private ContactInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        intent = getIntent();
        targetId = intent.getStringExtra("TargetName");
        if (!MyString.hasData(targetId)) {
            return;
        }

        info = ContactInfoSqlManager.getContactInfoFormuserName(targetId);
        if (info == null) {
            ContactInfoBiz.getContactInfo(targetId, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {
                    if (info != null) {
                        setTopBar(info);
                    }
                }
            });
        } else {

            setTopBar(info);
        }
        initView();
        setData();
        initLacalMessage();
        setRongCloudListener();

    }

    private void setTopBar(ContactInfo info) {
            getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, info.getName(), null, R.drawable.android_head_friendlist, this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    private void setData() {
        if (list == null) {
            list = new ArrayList<>();
        }
        mChattingAdapter = new ChattingAdapter(list, TestActivity.this);
        lvChat.setAdapter(mChattingAdapter);
    }

    private void initView() {
        initEmoticonsKeyBoardBar();
        initListView();

    }


    /**
     * 设置融云监听器
     */
    private void setRongCloudListener() {
        RongIMClient.setOnReceiveMessageListener(this);// 设置消息接收监听器。
    }

    /**
     * 返回聊天消息适配器
     *
     * @return the mChattingAdapter
     */
    public ChattingAdapter getChattingAdapter() {
        return mChattingAdapter;
    }

    private void initEmoticonsKeyBoardBar() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
//        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, SimpleCommonUtils.getCommonEmoticonClickListener(ekBar.getEtChat())));
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        gridView = new SimpleAppsGridView(this);
        ekBar.addFuncView(gridView);
        gridView.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AppBean item = (AppBean) parent.getAdapter().getItem(position);
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_pic))) {
                    CurrencyHelp.openImageActivity(TestActivity.this, true);
                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_vedio))) {

                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_file))) {

                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_location))) {

                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_card))) {

                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_read_after_fire))) {

                }
                if (item.getFuncName().equals(getResources().getString(R.string.avatar_desc))) {

                }


            }
        });
        ekBar.setOnMaxParentHeightChangeListener(this);
        // 输入框的监听
        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });

        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSendBtnClick(ekBar.getEtChat().getText().toString());
                ekBar.getEtChat().setText("");
            }
        });

        ekBar.getBtnVoice().setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {

//                SChattingHelp.sendVoiceMessage(targetId, filePath, (int) seconds, Conversation.ConversationType.PRIVATE, new SendMessageCallBack() {
//                    @Override
//                    public void sendMessage(Message message) {
//
//                        mChattingAdapter.addMessage(message);
//                    }
//                });
            }
        });

        //ToolBarView 添加加号
        ekBar.getEmoticonsToolBarView().addFixedToolItemView(false, R.drawable.icon_add, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "ADD", Toast.LENGTH_SHORT).show();
            }
        });
        //ToolBarView 添加设置
        ekBar.getEmoticonsToolBarView().addToolItemView(R.drawable.icon_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "SETTING", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initListView() {
        MyHeader header = new MyHeader(this);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);

        ptrFrameLayout.setOffsetToRefresh(DensityUtil.dip2px(40F));    //
        ptrFrameLayout.setOffsetToKeepHeaderWhileLoading(DensityUtil.dip2px(70F));

        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                // 加载更多的消息
                getHistoryMessages(Conversation.ConversationType.PRIVATE, targetId, lastMessageId, 20);
            }
        });

    }

    /**
     * 获取会话中，从指定消息之前、指定数量的最新消息实体
     */
    private void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count) {

        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if (messageList == null || messageList.isEmpty()){
                    ptrFrameLayout.refreshComplete();
                    return;
                }
                Message message = messageList.get(messageList.size() - 1);
                lastMessageId = message.getMessageId();
                mChattingAdapter.addData(messageList);
                ptrFrameLayout.refreshComplete();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ptrFrameLayout.refreshComplete();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scrollToBottom();
        PhotoPickUtils.onActivityResult(requestCode, resultCode, data, new PhotoPickUtils.PickHandler() {


            @Override
            public void onPickSuccess(ArrayList<String> photos) {//已经预先做了null或size为0的判断

                for (int i = 0; i < photos.size(); i++) {
                    String photo = photos.get(i);
                    try {
                        SChattingHelp.sendImageMessage(TestActivity.this, Conversation.ConversationType.PRIVATE, targetId, photo, new SendMessageCallBack() {
                            @Override
                            public void onSuccess(ShareLockMessage shareLockMessage) {

                                shareLockMessage.getMessage().setSentStatus(Message.SentStatus.SENT);
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);
                            }

                            @Override
                            public void onError(ShareLockMessage shareLockMessage, RongIMClient.ErrorCode code) {
                                shareLockMessage.getMessage().setSentStatus(Message.SentStatus.SENT);
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);
                            }

                            @Override
                            public void onProgress(ShareLockMessage shareLockMessage, int arg0) {
                                shareLockMessage.setProgress(arg0);
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);


                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onPreviewBack(ArrayList<String> photos) {
                Toast.makeText(TestActivity.this, photos.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPickFail(String error) {
                Toast.makeText(TestActivity.this, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPickCancle() {
                Toast.makeText(TestActivity.this, "取消选择", Toast.LENGTH_LONG).show();
            }


        });
    }

    /**
     * 初始化本地数据库中的消息
     */
    private void initLacalMessage() {
        SChattingHelp.getLatestMessages(Conversation.ConversationType.PRIVATE, targetId, new GetLatestMessagesCallBack() {
            @Override
            public void getMessagesCallBack(List<Message> data) {
                if (data == null || data.isEmpty()) return;

                Message message = data.get(data.size() - 1);
                lastMessageId = message.getMessageId();
//                    Collections.reverse(data);
                mChattingAdapter.addData(data);
                scrollToBottom();
            }
        });
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
            // 删除按钮
            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage(((EmoticonEntity) o).getIconUri());
                    }
                } else {
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };


    private void OnSendBtnClick(String msg) {


        if (!TextUtils.isEmpty(msg)) {

            LogUtil.i(targetId);
//            SChattingHelp.sendTextMessage(TestActivity.this, Conversation.ConversationType.PRIVATE, targetId, msg, new SendMessageCallBack() {
//                @Override
//                public void sendMessage(Message message) {
//                    mChattingAdapter.addMessage(message);
//                }
//            });

            scrollToBottom();
        }
    }

    private void OnSendImage(String image) {
        if (!TextUtils.isEmpty(image)) {
            OnSendBtnClick("[img]" + image);
        }
    }

    private void scrollToBottom() {
        lvChat.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvChat.setAdapter(lvChat.getAdapter());
                lvChat.setSelection(lvChat.getCount());

            }
        }, 500);
    }

    @Override
    public void OnFuncPop(int i) {

        Toast.makeText(TestActivity.this, "OnFuncPop" + i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnFuncClose() {
        Toast.makeText(TestActivity.this, "OnFuncClose", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMaxParentHeightChange(int i) {
        Toast.makeText(TestActivity.this, "onMaxParentHeightChange", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ekBar.reset();

    }


    /**
     * 收消息
     * @param message
     * @param i
     * @return
     */
    @Override
    public boolean onReceived(Message message, int i) {
        mChattingAdapter.addMessage(message);
        return false;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.left:
                hideSoftKeyboard();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.right_img:
                // 查看聊天信息
  startActivity(new Intent(this, PrivateChattingInfoActivity.class).putExtra("TargetName",targetId));
                break;
        }
    }


    @Override
    public void onSuccess(ShareLockMessage message) {

    }

    @Override
    public void onError(ShareLockMessage message, RongIMClient.ErrorCode code) {

    }

    @Override
    public void onProgress(ShareLockMessage message, int arg0) {

    }
}
