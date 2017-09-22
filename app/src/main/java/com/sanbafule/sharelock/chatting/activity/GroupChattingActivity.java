package com.sanbafule.sharelock.chatting.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.Constants;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.chatting.Interface.GetLatestMessagesCallBack;
import com.sanbafule.sharelock.chatting.Interface.ReceiveMessageCallback;
import com.sanbafule.sharelock.chatting.Interface.SendMessageCallBack;
import com.sanbafule.sharelock.chatting.adapter.ChattingAdapter;
import com.sanbafule.sharelock.chatting.help.ReceiveMessageHelp;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.AppBean;
import com.sanbafule.sharelock.chatting.modle.InsertSomeone;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.chatting.view.AudioRecorderButton;
import com.sanbafule.sharelock.chatting.view.SimpleAppsGridView;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.CurrencyHelp;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferenceSettings;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferences;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.Code;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.SimpleCommonUtils;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.MyKeyBoard;
import com.sanbafule.sharelock.view.header.MyHeader;
import com.sanbafule.sharelock.view.header.PtrDefaultHandler;
import com.sanbafule.sharelock.view.header.PtrFrameLayout;
import com.sanbafule.sharelock.view.header.PtrHandler;
import com.sj.emoji.EmojiBean;

import java.io.File;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import me.iwf.photopicker.PhotoPickUtils;
import pub.devrel.easypermissions.EasyPermissions;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;


/**
 * 此界面的groupId 是加前缀的
 */
public class GroupChattingActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener,
        ReceiveMessageCallback,
        AutoHeightLayout.OnMaxParentHeightChangeListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks {

    private static final String TAG = "GroupChattingActivity";
    @Bind(R.id.lv_chat)
    ListView lvChat;
    @Bind(R.id.ek_bar)
    public MyKeyBoard ekBar;
    @Bind(R.id.rotate_header_list_view_frame)
    PtrFrameLayout ptrFrameLayout;


    private ChattingAdapter mChattingAdapter;
    private SimpleAppsGridView gridView;
    private List<ShareLockMessage> list;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private Intent intent;
    private String targetId;
    // 最后一条消息的Id;
    private int lastMessageId;

    private ContactInfo info;

    boolean isGroupChat; //判断是否为群组聊天
    public static GroupChattingActivity mInstance;
    //@ someone
    private InsertSomeone mInsertSomeone;
    public boolean mSetAtSomeone = false;
    private boolean mHandlerDelChar = false;
    public ArrayList<String> atList = new ArrayList<>();
    public boolean mAtsomeone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        intent = getIntent();
        targetId = intent.getStringExtra(SString.TARGETNAME);
        if (!MyString.hasData(targetId)) {
            return;
        }
        isGroupChat = ShareLockManager.isGroupChat(targetId);
        //添加了群组的判断
        if (isGroupChat) {
            //注册群组  注册解散群组  注册退出群组  注册清除消息
            registerReceiver(new String[]{ReceiveAction.ACTION_DISMISS_GROUP, ReceiveAction.ACTION_QUIT_GROUP, ReceiveAction.ACTION_CLEAR_MESSAGE});
        } else {
            //注册清除消息  注册删除好友广播
            registerReceiver(new String[]{ReceiveAction.ACTION_CLEAR_MESSAGE, ReceiveAction.ACTION_DELETE_FRIEND});


        }
        initView();
        setRongCloudListener();
        initLacalMessage();
        setData();
        lvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ekBar.reset();
                return false;
            }
        });
    }

    private void initTopBar() {
        if (isGroupChat) {
            GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(targetId.substring(SString.GROUP_PRE.length()));
            if (groupInfo == null) {
                GroupInfoBiz.getGroupInfo(ShareLockManager.subGroupId(targetId), new GetGroupInfoListener() {
                    @Override
                    public void getGroupInfo(GroupInfo groupInfo) {
                        setTopBar(groupInfo.getG_name());
                    }
                });
            } else {
                setTopBar(groupInfo.getG_name());
            }

        } else {
            info = ContactInfoSqlManager.getContactInfoFormuserName(targetId);
            if (info == null) {
                ContactInfoBiz.getContactInfo(targetId, new GetContactInfoListener() {
                    @Override
                    public void getContactInfo(ContactInfo contactInfo) {
                        if (info != null) {
                            setTopBar(info.getU_nickname());
                        }
                    }
                });
            } else {

                setTopBar(info.getName());
            }
        }

    }


    private void setTopBar(String title) {
        if (isGroupChat) {
            if (!TextUtils.isEmpty(title)) {
                getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, getString(R.string.group_details_title), -1, this);
                if (GroupSqlManager.isExitGroup(ShareLockManager.subGroupId(targetId))) {
                    getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, getString(R.string.group_details_title), -1, this);
                } else {
                    getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, null, -1, this);
                }

            }
        } else {
            if (MyString.hasData(title)) {
                if (ContactSqlManager.hasThisFriend(targetId)) {

                    getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, null, R.drawable.android_head_friendlist, this);
                } else {
                    getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, title, null, -1, this);

                }
            }
        }


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    private void setData() {
        if (list == null) {
            list = new ArrayList<>();
        }
        mChattingAdapter = new ChattingAdapter(list, GroupChattingActivity.this);
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
        //注册消息接收者
        ReceiveMessageHelp.getInstance(this).setReceiveMessageCallback(this);

        RongIMClient.setOnRecallMessageListener(new RongIMClient.OnRecallMessageListener() {
            @Override
            public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
                LogUtil.i("没有回调");
                return false;
            }
        });
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
                    CurrencyHelp.openImageActivity(GroupChattingActivity.this, true);
                }
                if (item.getFuncName().equals(getResources().getString(R.string.app_panel_vedio))) {
                    //检查权限
                    if (EasyPermissions.hasPermissions(GroupChattingActivity.this, Manifest.permission.CAMERA)) {
                        //有权限  启动录制小视频
                        if (EasyPermissions.hasPermissions(GroupChattingActivity.this, Manifest.permission.RECORD_AUDIO)) {
                            //有权限  启动录制小视频
                            ShareLockManager.getInstance().startRecodeVideoActivity(GroupChattingActivity.this);

                        } else {
                            //没有 申请权限
                            EasyPermissions.requestPermissions(GroupChattingActivity.this, "录制小视频需要录音权限",
                                    Code.Permission_Video_Code, Manifest.permission.RECORD_AUDIO);

                        }
                    } else {
                        //没有 申请权限
                        EasyPermissions.requestPermissions(GroupChattingActivity.this, "录制小视频需要摄像头权限",
                                Code.Permission_Video_Code, Manifest.permission.CAMERA);
                    }
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
        //设置@someone的实体
        mInsertSomeone = new InsertSomeone();
        //添加输入框的监听
        ekBar.getEtChat().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //@ someone 的处理
                String text = String.valueOf(s); //当前的文本
                String value = text.substring(start, start + count);//输入的字符
                if (("@".equals(value) && isGroupChat && !text.equals(mInsertSomeone.getLastContent()) && !isSetAtSomeoneing())) {
                    mInsertSomeone.setLastContent(text);
                    mInsertSomeone.setInsertPos(start + 1);
                    boolean handler = (text == null || start < 0 || text.length() < start);
                    if (!handler) {
                        ToastUtil.showMessage("fsdfs");
                        Intent action = new Intent();
                        action.setClass(GroupChattingActivity.this, AtSomeoneUI.class);
                        action.putExtra(SString.GROUP_ID, ShareLockManager.subGroupId(targetId));
//                        action.putExtra(AtSomeoneUI.EXTRA_CHAT_USER, CCPAppManager.getUserId());
                        startActivityForResult(action, 212);
                    }
                    return;
                } else if (!text.equals(mInsertSomeone.getLastContent())) {
                    mInsertSomeone.setLastContent(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除@的集合
                String s = getLastText();
                //获取at的实体
                MentionedInfo mentionedInfo = ShareLockManager.getInstance().getAt(atList, s);
                OnSendBtnClick(s, mentionedInfo);
                if (atList != null) {
                    atList.clear();
                }
                ekBar.getEtChat().setText("");
            }
        });

        /**
         * 发送语音消息
         */
        ekBar.getBtnVoice().setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {


                SChattingHelp.sendVoiceMessage(GroupChattingActivity.this, targetId, filePath, (int) seconds, isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE, new SendMessageCallBack() {
                    @Override
                    public void onSuccess(ShareLockMessage shareLockMessage) {
                        shareLockMessage.getMessage().setSentStatus(Message.SentStatus.SENT);
                        List<ShareLockMessage> list = mChattingAdapter.getList();
                        mChattingAdapter.refreshData(list);
                    }

                    @Override
                    public void onError(ShareLockMessage shareLockMessage, RongIMClient.ErrorCode code) {
                        blackListOrBan(code);
                        shareLockMessage.getMessage().setSentStatus(Message.SentStatus.FAILED);
                        List<ShareLockMessage> list = mChattingAdapter.getList();
                        mChattingAdapter.refreshData(list);
                    }

                    @Override
                    public void onProgress(ShareLockMessage message, int arg0) {

                    }
                });
                scrollToBottom();
            }
        });

        //ToolBarView 添加加号
        ekBar.getEmoticonsToolBarView().addFixedToolItemView(false, R.drawable.icon_add, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加更多表情
                startActivity(new Intent(GroupChattingActivity.this, AddEmoticonsActivity.class));
            }
        });
        //ToolBarView 添加设置
        ekBar.getEmoticonsToolBarView().addToolItemView(R.drawable.icon_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到设置更多表情
                startActivity(new Intent(GroupChattingActivity.this, SettingEmoticonsActivity.class));
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (deleteAtSomeOne(event.getKeyCode(), event)) {
            return true;
        }
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
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
                // 加载更多的消息  //添加了群组的判断
                getHistoryMessages(isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE, targetId, lastMessageId, 20);
            }
        });
        lvChat.setOnItemClickListener(this);
        lvChat.setOnItemLongClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initTopBar();

    }

    /**
     * 获取会话中，从指定消息之前、指定数量的最新消息实体
     */
    private void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count) {

        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if (messageList == null || messageList.isEmpty()) {
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
        //发送小视频的结果回调
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Code.Permission_Video_Code) {
                String url = data.getStringExtra(SString.VIDEO_PATH);
                sendVideo(url);
                return;

            } else if (requestCode == 212) {
                String atSome = data.getStringExtra(SString.GROUP_MEMBER_ACCOUNT);
                if (TextUtils.isEmpty(atSome)) {
                    mInsertSomeone.setAtSomebody("");
//                    LogUtil.d(TAG, "@ [nobody]");
                    return;
                }
//                String nickName = GroupMemberSqlManager.getMenmberNickWithGroupId(mRecipients, selectUser);
//                if (TextUtils.isEmpty(nickName)) {
//                    nickName = MyString.delectFriendName(selectUser);
//                }
                mInsertSomeone.setAtSomebody(atSome);
                postSetAtSome();
                return;
            }
        }


        PhotoPickUtils.onActivityResult(requestCode, resultCode, data, new PhotoPickUtils.PickHandler() {


            @Override
            public void onPickSuccess(ArrayList<String> photos) {//已经预先做了null或size为0的判断

                for (int i = 0; i < photos.size(); i++) {
                    String photo = photos.get(i);
                    try {
                        SChattingHelp.sendImageMessage(GroupChattingActivity.this, isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE, targetId, photo, new SendMessageCallBack() {
                            @Override
                            public void onSuccess(ShareLockMessage shareLockMessage) {
                                shareLockMessage.getMessage().setSentStatus(Message.SentStatus.SENT);
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);
                                scrollToBottom();
                            }

                            @Override
                            public void onError(ShareLockMessage shareLockMessage, RongIMClient.ErrorCode code) {
                                blackListOrBan(code);
                                shareLockMessage.getMessage().setSentStatus(Message.SentStatus.FAILED);
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);
                                scrollToBottom();
                            }

                            @Override
                            public void onProgress(ShareLockMessage shareLockMessage, int arg0) {
                                List<ShareLockMessage> list = mChattingAdapter.getList();
                                mChattingAdapter.refreshData(list);


                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scrollToBottom();
                }


            }

            @Override
            public void onPreviewBack(ArrayList<String> photos) {
                Toast.makeText(GroupChattingActivity.this, photos.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPickFail(String error) {
                Toast.makeText(GroupChattingActivity.this, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPickCancle() {
                Toast.makeText(GroupChattingActivity.this, "取消选择", Toast.LENGTH_LONG).show();
            }


        });


    }


    /**
     * 初始化本地数据库中的消息
     */
    private void initLacalMessage() { //添加了群组的判断
        SChattingHelp.getLatestMessages(isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE,
                targetId, new GetLatestMessagesCallBack() {
                    @Override
                    public void getMessagesCallBack(List<Message> data) {
                        if (data == null || data.isEmpty()) return;
                        Message message = data.get(data.size() - 1);
                        lastMessageId = message.getMessageId();
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
                // 大图表情
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage((EmoticonEntity) o);
                    }
                } else {
                    // 小图表情
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


    /**
     * 发送文字消息
     *
     * @param msg
     */

    private void OnSendBtnClick(String msg, MentionedInfo mentionedInfo) {


        if (!TextUtils.isEmpty(msg)) {

            LogUtil.i(targetId);
            SChattingHelp.sendTextMessage(GroupChattingActivity.this, isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE, targetId, msg, mentionedInfo, new SendMessageCallBack() {
                @Override
                public void onSuccess(ShareLockMessage message) {
                    message.getMessage().setSentStatus(Message.SentStatus.SENT);
                    List<ShareLockMessage> list = mChattingAdapter.getList();
                    mChattingAdapter.refreshData(list);
                }

                @Override
                public void onError(ShareLockMessage message, RongIMClient.ErrorCode code) {
                    blackListOrBan(code);
                    message.getMessage().setSentStatus(Message.SentStatus.FAILED);
                    List<ShareLockMessage> list = mChattingAdapter.getList();
                    mChattingAdapter.refreshData(list);
                }

                @Override
                public void onProgress(ShareLockMessage message, int arg0) {

                }
            });

            scrollToBottom();
        }
    }

    /**
     * 发送gif消息
     *
     * @param image
     */


    private void OnSendImage(EmoticonEntity image) {
        if (!TextUtils.isEmpty(image.getContent()) && !TextUtils.isEmpty(image.getIconUri())) {
            sendGifFace(image);
        }
        scrollToBottom();
    }

    /**
     * 发送gif表情
     */
    private void sendGifFace(EmoticonEntity image) {

        SChattingHelp.sendGifMessage(this, isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE, targetId, image, new SendMessageCallBack() {
            @Override
            public void onSuccess(ShareLockMessage message) {
                message.getMessage().setSentStatus(Message.SentStatus.SENT);
                List<ShareLockMessage> list = mChattingAdapter.getList();
                mChattingAdapter.refreshData(list);
            }

            @Override
            public void onError(ShareLockMessage message, RongIMClient.ErrorCode code) {
                blackListOrBan(code);
                message.getMessage().setSentStatus(Message.SentStatus.FAILED);
                List<ShareLockMessage> list = mChattingAdapter.getList();
                mChattingAdapter.refreshData(list);
            }

            @Override
            public void onProgress(ShareLockMessage message, int arg0) {
                message.setProgress(arg0);
                List<ShareLockMessage> list = mChattingAdapter.getList();
                mChattingAdapter.refreshData(list);

            }
        });
        scrollToBottom();
    }

    /**
     * 发送小视频表情
     */
    private void sendVideo(String url) {
        File file = new File(url);
        if (!file.exists() || file.length() <= 0) {
            ToastUtil.showMessage("该视频不存在");
            return;
        }
        SChattingHelp.sendVideoMessage(this, targetId, isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE,
                url, new SendMessageCallBack() {
                    @Override
                    public void onSuccess(ShareLockMessage message) {
                        message.getMessage().setSentStatus(Message.SentStatus.SENT);
                        List<ShareLockMessage> list = mChattingAdapter.getList();
                        mChattingAdapter.refreshData(list);
                    }

                    @Override
                    public void onError(ShareLockMessage message, RongIMClient.ErrorCode code) {
                        blackListOrBan(code);
                        message.getMessage().setSentStatus(Message.SentStatus.FAILED);
                        List<ShareLockMessage> list = mChattingAdapter.getList();
                        mChattingAdapter.refreshData(list);
                    }

                    @Override
                    public void onProgress(ShareLockMessage message, int arg0) {
                        List<ShareLockMessage> list = mChattingAdapter.getList();
                        mChattingAdapter.refreshData(list);
                    }
                });
        scrollToBottom();
    }

    private void scrollToBottom() {
        lvChat.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lvChat != null && lvChat.getAdapter() != null) {
                    lvChat.setAdapter(lvChat.getAdapter());
                    lvChat.setSelection(lvChat.getCount());
                }

            }
        }, 500);
    }

    @Override
    public void OnFuncPop(int i) {

//        ViewGroup.MarginLayoutParams params = new PtrFrameLayout.LayoutParams(ptrFrameLayout.getLayoutParams());
//        params.bottomMargin=i;
//        ptrFrameLayout.setLayoutParams(params);
        scrollToBottom();
//        Toast.makeText(GroupChattingActivity.this, "OnFuncPop" + i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnFuncClose() {
//        Toast.makeText(GroupChattingActivity.this, "OnFuncClose", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMaxParentHeightChange(int i) {
        Toast.makeText(GroupChattingActivity.this, "onMaxParentHeightChange" + i, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ekBar.reset();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
            case R.id.right_img:

                // 清空消息
                if (isGroupChat) {
                    ShareLockManager.startGroupDetailsActivity(this, targetId);
                } else {
                    // 查看聊天信息
                    startActivity(new Intent(this, PrivateChattingInfoActivity.class).putExtra(SString.TARGETNAME, targetId));
                }

                break;


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        targetId = null;
        mInstance = null;

    }

    // 广播接收器  接受群组列表插入完成的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_QUIT_GROUP.equals(intent.getAction())) {
            /**1  接受到群组退出的广播
             2  关闭聊天界面
             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId)) {
                if (delGroupId.equals(ShareLockManager.subGroupId(targetId))) {
                    finish();
                }
            }
        } else if (ReceiveAction.ACTION_DISMISS_GROUP.equals(intent.getAction())) {
            /**1  接受到群组解散的广播
             2   关闭聊天界面

             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId)) {
                if (delGroupId.equals(ShareLockManager.subGroupId(targetId))) {
                    finish();
                }
            }
        } else if (ReceiveAction.ACTION_CLEAR_MESSAGE.equals(intent.getAction())) {
            String target = intent.getStringExtra(SString.TARGETNAME);
            Conversation.ConversationType type = (Conversation.ConversationType) intent.getSerializableExtra(SString.TYPE);
            if (type != null && !TextUtils.isEmpty(target)) {
                if (type == Conversation.ConversationType.GROUP) {
                    //群组
                    if (targetId.equals(target)) {
                        if (mChattingAdapter != null) {
                            mChattingAdapter.cleaData();
                        }
                    }
                } else if (type == Conversation.ConversationType.PRIVATE) {
                    //
                    if (targetId.equals(target)) {
                        if (mChattingAdapter != null) {
                            mChattingAdapter.cleaData();
                        }
                    }
                }
            }
        } else if (ReceiveAction.ACTION_DELETE_FRIEND.equals(intent.getAction())) {
            /**1  删除好友
             */
            String delFriend = intent.getStringExtra(SString.TARGETNAME);
            if (!TextUtils.isEmpty(delFriend)) {
                if (delFriend.equals(targetId)) {
                    finish();
                }
            }
        }
    }

    @Override
    public void receiveMessage(final Message message) {
        //获取收到消息的id 判断是否是当前聊天的消息
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String targetId = message.getTargetId();
                if (GroupChattingActivity.this.targetId.equals(targetId)) {
                    mChattingAdapter.addMessage(message);
                    //收到消息更新消息的状态
                    RongIMClient.getInstance().clearMessagesUnreadStatus(message.getConversationType(),
                            targetId, null);
                    scrollToBottom();
                }
            }
        });

    }


    public boolean deleteAtSomeOne(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                mHandlerDelChar = !(getCharAtCursor() != (char) (8197));
                return false;
            }
            if (event.getAction() == KeyEvent.ACTION_UP && mHandlerDelChar) {
                mHandlerDelChar = false;
                int selectionStart = getSelectionStart();
                String text = getLastText().substring(0, selectionStart);
                int atIndex = text.lastIndexOf('@');
                if (atIndex < text.length() && atIndex >= 0) {
                    String subStartText = text.substring(0, atIndex);
                    String subSecondText = getLastText().substring(selectionStart);
                    StringBuilder sb = new StringBuilder();
                    sb.append(subStartText).append(subSecondText);
                    setLastText(sb.toString());
                    ekBar.getEtChat().setSelection(atIndex);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * item 点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    /**
     * item 长按事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            final int itemPosition = position;
//            if (mChattingAdapter != null) {
//                int headerViewsCount = lvChat.getHeaderViewsCount();
//                if (itemPosition < headerViewsCount) {
//                    return false;
//                }
//                int _position = itemPosition - headerViewsCount;
//
//                if (mChattingAdapter == null || mChattingAdapter.getItem(_position) == null) {
//                    return false;
//                }
//                ShareLockMessage item = mChattingAdapter.getItem(_position);
//                Message message = item.getMessage();
//                // 文字消息
//                if (message.getContent() instanceof TextMessage) {
//                    if (message.getMessageDirection() == Message.MessageDirection.SEND) {
//                        handRightText(item);
//                    } else {
//                        handLeftText(item);
//                    }
//
//                    // 图片消息
//                }else if(message.getContent() instanceof ImageMessage){
//                    if (message.getMessageDirection() == Message.MessageDirection.SEND) {
//                        handRightImage(item);
//                    } else {
//                        handLeftImageShowTranspond(item);
//                    }
//                    //文件消息
//                }else if (message.getContent() instanceof FileMessage){
//                    if (message.getMessageDirection() == Message.MessageDirection.SEND) {
//                        handRightFile(item);
//                    } else {
//                        handLeftFile(item);
//                    }
//                }
//
//                return true;
//            }
        return false;
    }

    /**
     * 左边语音的长按处理
     *
     * @param shareLockMessage
     */
    public void handLeftVoice(final ShareLockMessage shareLockMessage) {
        {
            final Message message = shareLockMessage.getMessage();
            final int[] messageIds = {message.getMessageId()};
            boolean isEarpiece = ShareLockPreferences.getSharedPreferences().
                    getBoolean(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getId(),
                            (Boolean) ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getDefaultValue());
            if (!isEarpiece) {
                new MaterialDialog.Builder(this)
                        .items(R.array.send_voice_long_click_01)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        // 切换为听筒模式
                                        try {
                                            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.TRUE, Boolean.TRUE);
                                        } catch (InvalidClassException e) {
                                            e.printStackTrace();
                                        }
                                        ToastUtil.showMessage(R.string.fmt_route_phone);
                                        break;
                                    case 1:
                                        // 收藏
                                        break;
                                    case 2:
                                        // 撤回
                                        recallMessage(message);
                                        break;
                                    case 3:
                                        //删除
                                        removeMessage(messageIds, shareLockMessage);
                                        break;
                                    case 4:
//                              更多
                                        ToastUtil.showMessage("更多");
                                        break;

                                }
                            }
                        })
                        .show();
            } else {
                new MaterialDialog.Builder(this)
                        .items(R.array.send_voice_long_click_02)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        // 切换为扬声器模式
                                        try {
                                            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.FALSE, Boolean.TRUE);
                                        } catch (InvalidClassException e) {
                                            e.printStackTrace();
                                        }
                                        ToastUtil.showMessage(R.string.fmt_route_speaker);
                                        break;
                                    case 1:
                                        // 收藏
                                        break;
                                    case 2:
                                        // 撤回
                                        recallMessage(message);
                                        break;
                                    case 3:
                                        // 删除
                                        removeMessage(messageIds, shareLockMessage);
                                        break;
                                    case 4:
//                              更多
                                        ToastUtil.showMessage("更多");
                                        break;

                                }
                            }
                        })
                        .show();
            }

        }

    }

    /**
     * 右边语音的长按处理
     *
     * @param shareLockMessage
     */
    public void handRightVoice(final ShareLockMessage shareLockMessage) {

        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        boolean isEarpiece = ShareLockPreferences.getSharedPreferences().
                getBoolean(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getId(),
                        (Boolean) ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getDefaultValue());
        if (!isEarpiece) {
            new MaterialDialog.Builder(this)
                    .items(R.array.receive_voice_long_click_01)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:

                                    // 切换为听筒模式
                                    try {
                                        ShareLockPreferences.savePreference(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.TRUE, Boolean.TRUE);
                                    } catch (InvalidClassException e) {
                                        e.printStackTrace();
                                    }
                                    ToastUtil.showMessage(R.string.fmt_route_phone);
                                    break;
                                case 1:
                                    // 收藏
                                    break;
                                case 2:
//                             删除
                                    removeMessage(messageIds, shareLockMessage);
                                    break;
                                case 3:
//                              更多
                                    ToastUtil.showMessage("更多");
                                    break;

                            }
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(this)
                    .items(R.array.receive_voice_long_click_02)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    // 切换为扬声器模式
                                    try {
                                        ShareLockPreferences.savePreference(ShareLockPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.FALSE, Boolean.TRUE);
                                    } catch (InvalidClassException e) {
                                        e.printStackTrace();
                                    }
                                    ToastUtil.showMessage(R.string.fmt_route_speaker);
                                    break;
                                case 1:
                                    // 收藏
                                    break;
                                case 2:
//                             删除
                                    removeMessage(messageIds, shareLockMessage);
                                    break;
                                case 3:
//                              更多
                                    ToastUtil.showMessage("更多");
                                    break;

                            }
                        }
                    })
                    .show();
        }


    }


    /**
     * 左边文件的长按处理
     *
     * @param shareLockMessage
     */
    public void handLeftFile(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.receive_file_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                //转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.mInstance, message);
                                break;
                            case 1:
                                // 收藏
                                break;
                            case 2:
//                             删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 4:
//                              更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();
    }

    /**
     * 左边图片的长按处理
     *
     * @param shareLockMessage
     */
    public void handLeftImageShowTranspond(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.receive_image_long_click_show_transpond)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                //转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.mInstance, message);
                                break;
                            case 1:
                                // 收藏
                                break;
                            case 2:
//                             删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 4:
//                              更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();

    }

    /**
     * 左边图片的长按处理
     *
     * @param shareLockMessage
     */
    public void handLeftImage(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.receive_image_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                // 收藏
                                break;
                            case 1:
                                //删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 2:
                                //更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();

    }

    /**
     * 左边文字的长按处理
     *
     * @param shareLockMessage
     */
    public void handLeftText(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(GroupChattingActivity.this)
                .items(R.array.receive_text_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                if (message.getContent() instanceof TextMessage) {
                                    // 复制文本
                                    SChattingHelp.copyText(GroupChattingActivity.this, "", ((TextMessage) message.getContent()).getContent());
                                }

                                break;
                            case 1:
                                // 转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.this, message);
                                break;
                            case 2:
//                                收藏
                                break;
                            case 3:
// 删除消息
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 4:
//                             更多
                                ToastUtil.showMessage("更多");
                                break;
                        }
                    }
                })
                .show();
    }


    public void handRightFile(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.send_file_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                //转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.mInstance, message);
                                break;
                            case 1:
                                // 收藏
                                break;
                            case 2:
//                             撤回
                                recallMessage(message);
                                break;
                            case 3:
//                             删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 4:
//                             更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();
    }

    public void handRightImageShowTranspond(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.send_image_long_click_show_transpond)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                //转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.mInstance, message);
                                break;
                            case 1:
                                // 收藏
                                break;
                            case 2:
//                             撤回
                                recallMessage(message);
                                break;
                            case 3:
//                             删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 4:
                                // 更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();

    }

    public void handRightImage(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(this)
                .items(R.array.send_image_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                // 收藏
                                break;
                            case 1:
                                //撤回
                                recallMessage(message);
                                break;
                            case 2:
                                //删除
                                removeMessage(messageIds, shareLockMessage);
                                break;
                            case 3:
                                // 更多
                                ToastUtil.showMessage("更多");
                                break;

                        }
                    }
                })
                .show();

    }

    /**
     * 长按处理文本消息
     *
     * @pam content
     */
    public void handRightText(final ShareLockMessage shareLockMessage) {
        final Message message = shareLockMessage.getMessage();
        final int[] messageIds = {message.getMessageId()};
        new MaterialDialog.Builder(GroupChattingActivity.this)
                .items(R.array.send_text_long_click)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                if (message.getContent() instanceof TextMessage) {
                                    // 复制文本
                                    SChattingHelp.copyText(GroupChattingActivity.this, "", ((TextMessage) message.getContent()).getContent());
                                }

                                break;
                            case 1:
                                // 转发
                                SChattingHelp.transmitMessage(GroupChattingActivity.mInstance, message);
                                break;
                            case 2:
//                                收藏
                                break;
                            case 3:
//                                撤回
                                recallMessage(message);
                                break;
                            case 4:
                                // 删除消息
                                removeMessage(messageIds, shareLockMessage);
//                                删除
                                break;
                        }
                    }
                })
                .show();
    }

    // 权限获取成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //小视频权限申请
        if (requestCode == Code.Permission_Video_Code) {
            if (perms.get(0).equals(Manifest.permission.CAMERA)) {

                if (EasyPermissions.hasPermissions(GroupChattingActivity.this, Manifest.permission.RECORD_AUDIO)) {
                    //有权限  启动录制小视频
                    ShareLockManager.getInstance().startRecodeVideoActivity(GroupChattingActivity.this);

                } else {
                    //没有 申请权限
                    EasyPermissions.requestPermissions(GroupChattingActivity.this, "录制小视频需要录音权限",
                            Code.Permission_Video_Code, Manifest.permission.RECORD_AUDIO);

                }
            } else if (perms.get(0).equals(Manifest.permission.RECORD_AUDIO)) {
                if (EasyPermissions.hasPermissions(GroupChattingActivity.this, Manifest.permission.CAMERA)) {
                    //有权限  启动录制小视频
                    ShareLockManager.getInstance().startRecodeVideoActivity(GroupChattingActivity.this);

                } else {
                    //没有 申请权限
                    EasyPermissions.requestPermissions(GroupChattingActivity.this, "录制小视频需要摄像头权限",
                            Code.Permission_Video_Code, Manifest.permission.CAMERA);

                }

            }

        }
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                "为了您的正常使用，需要获得相机权限,录音权限，请您进去设置->应用->三八妇乐->权限进行设置",
                R.string.go_setting, R.string.negative, perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 清楚数据
     */
    private void clear() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChattingAdapter.removeAllData();
            }
        });
    }

    /**
     * 撤回消息
     *
     * @param message
     */
    public void recallMessage(Message message) {
        showDiglog();
        RongIMClient.getInstance().recallMessage(message, null, new RongIMClient.ResultCallback<RecallNotificationMessage>() {
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                // 重新获取数据

                clear();
                SChattingHelp.getLatestMessages(isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE,
                        targetId, new GetLatestMessagesCallBack() {
                            @Override
                            public void getMessagesCallBack(List<Message> data) {
                                closeDialog();
                                if (data == null || data.isEmpty()) return;
                                Message message = data.get(data.size() - 1);
                                lastMessageId = message.getMessageId();
                                mChattingAdapter.addData(data);
                                scrollToBottom();

                            }
                        });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                closeDialog();
                ToastUtil.showMessage("撤回错误" + errorCode.getValue());
            }
        });

    }

    /**
     * 删除消息
     *
     * @param messageIds
     * @param shareLockMessage
     */
    public void removeMessage(int[] messageIds, final ShareLockMessage shareLockMessage) {
        showDiglog();
        RongIMClient.getInstance().deleteMessages(messageIds, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                closeDialog();
                mChattingAdapter.removeData(shareLockMessage);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                closeDialog();
                mChattingAdapter.notifyDataSetChanged();
            }
        });
    }


    /********************
     * @ @@@@someOne
     **************************/

    //正在@ 某人
    public boolean isSetAtSomeoneing() {
        return mSetAtSomeone;
    }

    //设置输入框的文本
    public void setLastText(String text, int selecton, boolean clear) {
        if (clear && (text == null || text.length() == 0 || ekBar.getEtChat() == null)) {
            ekBar.getEtChat().setText("");
            return;
        }
        mSetAtSomeone = true;
        EmoticonsEditText editText = ekBar.getEtChat();
        editText.setText(text);
        mSetAtSomeone = false;
        if ((selecton < 0) || (selecton > this.ekBar.getEtChat().getText().length())) {
            this.ekBar.getEtChat().setSelection(this.ekBar.getEtChat().getText().length());
            return;
        }
        this.ekBar.getEtChat().setSelection(selecton);
    }

    //设置输入框的文本
    public void setLastText(String text) {
        setLastText(text, -1, true);
    }

    /**
     * 处理@某人
     */
    private void postSetAtSome() {
        String atSomebody = mInsertSomeone.getAtSomebody();
        if (!TextUtils.isEmpty(atSomebody)) {
            String text = getLastText();
            int someInsertPosition = mInsertSomeone.getInsertPos();
            if (someInsertPosition > text.length()) {
                someInsertPosition = text.length();
            }
            String s = text.substring(0, someInsertPosition) + atSomebody
                    + (char) (8197);
            //添加at的人
            atList.add(atSomebody);
            String message = s
                    + text.substring(someInsertPosition, text.length());
            int selectoin = 1 + someInsertPosition + atSomebody.length();
            mInsertSomeone.setLastContent(message);
            setLastText(message, selectoin, false);
            mInsertSomeone.setLastContent(null);
            toggleSoftInput();
        }
    }

    /**
     * 获取@某人后面的空格
     */
    public char getCharAtCursor() {
        int i = getSelectionStart();
        if (i <= 0) {
            return 'x';
        }
        return getLastText().charAt(i - 1);
    }

    /**
     * 获取光标的位置
     *
     * @return
     */
    public int getSelectionStart() {
        return ekBar.getEtChat().getSelectionStart();
    }

    /**
     * 获得输入最后的文本
     *
     * @return
     */
    public final String getLastText() {
        if (ekBar.getEtChat() == null) {
            return "";
        }
        return ekBar.getEtChat().getText().toString();
    }

    /**
     * 分发键盘事件 只要是删除@someone
     */

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        //
//
//        return super.dispatchKeyEvent(event);
//    }

    /**
     * 黑名单显示的提示 Dialog
     */
    private void blackListOrBan(RongIMClient.ErrorCode code) {
        if (RongIMClient.ErrorCode.REJECTED_BY_BLACKLIST == code) {
            //黑名单
            if (mInstance != null) {
                DialogUtils.showBasicWithTile(mInstance,
                        getString(R.string.dialog_hint_title),
                        getString(R.string.black_list_tip),
                        R.string.positive, -1, true, null);
            }
        } else if (RongIMClient.ErrorCode.FORBIDDEN_IN_GROUP == code) {
            //群组禁言
            if (mInstance != null) {
                DialogUtils.showBasicWithTile(mInstance,
                        getString(R.string.dialog_hint_title),
                        getString(R.string.group_ban_tip),
                        R.string.positive, -1, true, null);
            }

        }
    }
}
