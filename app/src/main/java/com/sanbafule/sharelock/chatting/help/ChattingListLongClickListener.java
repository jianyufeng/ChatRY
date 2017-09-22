package com.sanbafule.sharelock.chatting.help;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.Interface.GetLatestMessagesCallBack;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.modle.ViewHolderTag;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.util.ToastUtil;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RecallNotificationMessage;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ChattingListLongClickListener implements View.OnLongClickListener {

    /**聊天界面*/
    private GroupChattingActivity mContext;

    public ChattingListLongClickListener(GroupChattingActivity activity ) {
        mContext = activity;
    }
    @Override
    public boolean onLongClick(View v) {
        ViewHolderTag holder = (ViewHolderTag) v.getTag();
        final Message iMessage = holder.detail;

        switch (holder.type) {
            case ViewHolderTag.TagType.TAG_VIEW_FILE:
//                ShowMyCaoZuo(iMessage);


                return true;
            case ViewHolderTag.TagType.TAG_VOICE:
//                ShowMyVoiceCaoZuo(iMessage);
                ToastUtil.showMessage("语音的长按事件");

                return true;
            case ViewHolderTag.TagType.TAG_VIEW_PICTURE:
//                ShowMyCaoZuo(iMessage);

                return true;
            case ViewHolderTag.TagType.TAG_IM_LOCATION :
//                ShowMyCaoZuo(iMessage);


                return true;
            case ViewHolderTag.TagType.TAG_TEXT :
                ToastUtil.showMessage("Text 文本");
                return true;
            default:


                break;
        }
        return false;
    }




    /**
     * 撤回消息
     *
     * @param message
     */
    private void recallMessage(Message message) {
//        showDiglog();
        RongIMClient.getInstance().recallMessage(message, null, new RongIMClient.ResultCallback<RecallNotificationMessage>() {
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                // 重新获取数据
                ToastUtil.showMessage("撤回成功");
//                clear();
//                SChattingHelp.getLatestMessages(isGroupChat ? Conversation.ConversationType.GROUP : Conversation.ConversationType.PRIVATE,
//                        targetId, new GetLatestMessagesCallBack() {
//                            @Override
//                            public void getMessagesCallBack(List<Message> data) {
//                                closeDialog();
//                                if (data == null || data.isEmpty()) return;
//                                Message message = data.get(data.size() - 1);
//                                lastMessageId = message.getMessageId();
//                                mChattingAdapter.addData(data);
//                                scrollToBottom();
//
//                            }
//                        });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
//                closeDialog();
                ToastUtil.showMessage("撤回错误" + errorCode.getValue());
            }
        });

    }




//    private void ShowMyCaoZuo(final ECMessage iMessage) {
//        dialog = new ECListDialog(mContext, new String[]{"删除"});
//        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
//            @Override
//            public void onDialogItemClick(Dialog d, int position) {
//                handleContentMenuClick(iMessage);
//            }
//        });
//        dialog.setTitle("操作");
//        dialog.show();
//    }
//    private void ShowMyVoiceCaoZuo(final ECMessage iMessage) {
//        //获取当前的状态 判断是否是听筒模式
//        boolean isEarpiece = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getId(),
//                (Boolean) ECPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE.getDefaultValue());
//        dialog = new ECListDialog(mContext, new String[]{"听筒模式","外放模式","删除"},isEarpiece?0:1);
//        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
//            @Override
//            public void onDialogItemClick(Dialog d, int position) {
//                switch (position) {
//                    //听筒模式
//                    case 0:
//
//                        try {
//                            ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.TRUE, Boolean.TRUE);
//                        } catch (InvalidClassException e) {
//                            e.printStackTrace();
//                        }
//                        ToastUtil.showMessage(R.string.fmt_route_phone);
//
//                        break;
//                    //外放模式
//                    case 1:
//                        try {
//                            ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_NEW_MSG_EARPIECE, Boolean.FALSE, Boolean.TRUE);
//                            ToastUtil.showMessage(R.string.fmt_route_speaker);
//                        } catch (InvalidClassException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    //删除消息
//                    case 2:
//                        handleContentMenuClick(iMessage);
//                        break;
//                }
//            }
//        });
//        dialog.setTitle("操作");
//        dialog.show();
//    }

//
//    public void handleContentMenuClick(final ECMessage msg){
//        ((ChattingActivity) mContext).mChattingFragment.doDelMsgTips(msg,-1);
//
//    }

}
