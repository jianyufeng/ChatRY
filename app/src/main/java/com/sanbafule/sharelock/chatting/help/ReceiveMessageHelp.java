package com.sanbafule.sharelock.chatting.help;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.chatting.Interface.ReceiveMessageCallback;
import com.sanbafule.sharelock.chatting.util.MessageNotificationManager;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.RunningTaskInfo;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.util.VibratorUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandMessage;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.TextMessage;


/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/6 16:11
 * cd : 三八妇乐
 * 描述：
 */
public class ReceiveMessageHelp implements RongIMClient.OnReceiveMessageListener, RongIMClient.ConnectionStatusListener {

    private Context context;

    private ReceiveMessageHelp(Context context) {
        this.context = context;
    }

    public static ReceiveMessageHelp instance;

    private ReceiveMessageCallback receiveMessageCallback;

    public void setReceiveMessageCallback(ReceiveMessageCallback receiveMessageCallback) {
        this.receiveMessageCallback = receiveMessageCallback;
    }

    public static ReceiveMessageHelp getInstance(Context context) {
        if (instance == null) {
            instance = new ReceiveMessageHelp(context);
        }
        return instance;
    }

    @Override
    public boolean onReceived(Message message, int i) {


        if (message == null) return false;
        LogUtil.i(message.getSenderUserId());
        LogUtil.i(message.getConversationType().toString());
        LogUtil.i(message.getContent().toString());
        LogUtil.i(message.getObjectName());
        LogUtil.i(message.getTargetId());
        //对消分类预先处理
        sortMessage(message);

//        1.分类消息
//        Conversation.ConversationType conversationType = message.getConversationType();
//        switch (conversationType) {
//            case PRIVATE:
//                //2.显示通知
//                break;
//            case GROUP:
//                //2.显示通知
//                break;
//            case SYSTEM:
//                //2.显示通知
//
//                break;
//        }

        //3.暴露接口
        if (receiveMessageCallback != null) {
            receiveMessageCallback.receiveMessage(message);
        }
        //设置通知  在后台是提醒通知
        if (RunningTaskInfo.isBackground(SApplication.getInstance())) {
            MessageNotificationManager.getInstance().showMessageNotification(message);
        } else {
            //前台 震动
            VibratorUtil.Vibrate(SApplication.getInstance(), new long[]{100, 200, 100, 200}, false);
        }
        return false;
    }

    private void sortMessage(Message message) {

//        LogUtil.i("消息是" + message);
        MessageContent content = message.getContent();
        if (content instanceof CommandMessage) {
            //被挤出消息
            doCommandMessage(content);
        } else if (message.getConversationType() == Conversation.ConversationType.PRIVATE &&
                content instanceof CommandNotificationMessage) {
            //删除好友
            doDeleteFriend(message.getSenderUserId());
        } else if (message.getConversationType() == Conversation.ConversationType.SYSTEM &&
                message.getTargetId().startsWith(SString.GROUP_INFO_)) {
            //解散。移除群组
            try {
                doGroupDisOrQuit(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //处理解散或移除自己的系统消息
    private void doGroupDisOrQuit(MessageContent content) throws Exception {
        if (content instanceof TextMessage) {
            TextMessage m = (TextMessage) content;
            JSONObject object = new JSONObject(m.getContent());
            String groupId = object.getString("groupId");
            if (!TextUtils.isEmpty(groupId)) {
                GroupSqlManager.deleteGroup(groupId);
                GroupService.deleteMessage(Conversation.ConversationType.GROUP, ShareLockManager.addGroupPre(groupId));
                SApplication.getInstance().sendBroadcast(new Intent(ReceiveAction.ACTION_DISMISS_GROUP)
                        .putExtra(SString.GROUP_ID, groupId));
            }
        }

    }

    //删除好友的消息
    private void doDeleteFriend(String user) {
        if (TextUtils.isEmpty(user)) return;
        ContactSqlManager.deleteContact(user);
        GroupService.deleteMessage(Conversation.ConversationType.PRIVATE, user);
    }

    //处理在线挤出
    private void doCommandMessage(MessageContent content) {
        CommandMessage cmdMessage = (CommandMessage) content;
        LogUtil.i("消息是" + cmdMessage.getName());
        if (cmdMessage.getName().equals(SString.CMD_LOGOUT)) {
            String data = cmdMessage.getData();
            try {
                JSONObject object = new JSONObject(data);
                String userName = object.getString("username");
                String userToken = object.getString("usertoken");
                if (userName.equals(ShareLockManager.getInstance().getUserName()) && userToken.equals(ShareLockManager.getInstance().getToken())) {
                    Intent intent = new Intent(ReceiveAction.ACTION_KICK_OFF);
                    context.sendBroadcast(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接状态
     *
     * @param status
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        ToastUtil.showMessage("连接状态发生变化了");
        if (status == ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
            ToastUtil.showMessage("用户账户在其他设备登录，本机会被踢掉线");
            //账号异地登陆
            /*
            1 清除登录保存的信息
            2 发送踢出广播
            3 弹出对话框点击确认 登出
            4 发送踢出通知
            * */
            Intent intent = new Intent(ReceiveAction.ACTION_KICK_OFF);
            context.sendBroadcast(intent);
            MessageNotificationManager.getInstance().showKickoffNotification("账号在其他设备登录");
        } else {
            if (status == ConnectionStatus.NETWORK_UNAVAILABLE) {
                ToastUtil.showMessage("网络不可用。");

            } else if (status == ConnectionStatus.SERVER_INVALID) {
                ToastUtil.showMessage("服务器异常或无法连接。");
            } else if (status == ConnectionStatus.DISCONNECTED) {
                ToastUtil.showMessage("断开连接。");
            } else if (status == ConnectionStatus.TOKEN_INCORRECT) {
                ToastUtil.showMessage("Token 不正确。");
            } else if (status == ConnectionStatus.CONNECTING) {
                ToastUtil.showMessage("连接中。");
            } else if (status == ConnectionStatus.CONNECTED) {
                ToastUtil.showMessage("连接成功。");
            }
            Intent intent = new Intent(ReceiveAction.ACTION_CONNECT_STATE);
            context.sendBroadcast(intent);
        }
    }

}
