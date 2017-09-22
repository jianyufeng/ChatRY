package com.sanbafule.sharelock.chatting.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.NotificationUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


/**
 * 作者:Created by 简玉锋 on 2017/1/6 16:52
 * 邮箱: jianyufeng@38.hn
 */

public class MessageNotificationManager {
    private static NotificationManager mNotificationManager;
    private Context mContext;
    public static MessageNotificationManager mInstance;

    private Handler handler = new Handler(Looper.getMainLooper());

    public static MessageNotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new MessageNotificationManager(SApplication.getInstance());
        }

        return mInstance;
    }

    private MessageNotificationManager(Context context) {
        this.mContext = context;
    }

    //通知分类
    public final void showMessageNotification(Message latestMessage) {
        if (latestMessage.getConversationType().equals(Conversation.ConversationType.PRIVATE)) {
            showPrivateMessageNotification(latestMessage);
        } else if (latestMessage.getConversationType().equals(Conversation.ConversationType.GROUP)) {
            showGroupMessageNotification(latestMessage);
        } else if (latestMessage.getConversationType().equals(Conversation.ConversationType.SYSTEM)) {
            showSysMessageNotification(latestMessage);
        }

    }

    /**
     * 来通知的提醒
     *
     * @param fromUserName
     * @param latestMessage
     * @return
     */
    public final String getTickerText(String fromUserName, MessageContent latestMessage) {
        if (latestMessage instanceof TextMessage) {
            return mContext.getString(R.string.notification_fmt_one_txtType, fromUserName);
        } else if (latestMessage instanceof ImageMessage) {
            return mContext.getString(R.string.notification_fmt_one_imgType, fromUserName);
        } else if (latestMessage instanceof VoiceMessage) {
            return mContext.getString(R.string.notification_fmt_one_voiceType, fromUserName);
        } else if (latestMessage instanceof FileMessage) {
            if (((FileMessage) latestMessage).getType().equals(FileMessageType.GIF.toString())) {
                return mContext.getString(R.string.notification_fmt_one_GifType, fromUserName);
            } else if (((FileMessage) latestMessage).getType().equals(FileMessageType.VIDEO.toString())) {
                return mContext.getString(R.string.notification_fmt_one_VideoType, fromUserName);
            }
            return mContext.getString(R.string.notification_fmt_one_fileType, fromUserName);
        } else if (latestMessage instanceof LocationMessage) {
            return mContext.getString(R.string.notification_fmt_one_locationType, fromUserName);
        } else if (latestMessage instanceof InformationNotificationMessage) {
            return mContext.getString(R.string.str_system_message_group_notice);
        } else {
            //return contex.getString(R.string.app_name);
            return mContext.getPackageManager().getApplicationLabel(mContext.getApplicationInfo()).toString();
        }

    }

    /**
     * 通知的标题
     *
     * @param context
     * @param sessionUnreadCount
     * @param fromUserName
     * @return
     */
    public final String getContentTitle(Context context, int sessionUnreadCount, String fromUserName) {
        //很多人显示
        if (sessionUnreadCount > 1) {
            return context.getString(R.string.app_name);
        }
        //一个人显示
        return fromUserName;
    }

    /**
     * 通知的内容
     *
     * @return
     */
    public final String getContentText(MessageContent latestMessage) {

//        if (sessionCount > 1) {
//            //很多人发了很多条
//            return context.getResources().getQuantityString(
//                    R.plurals.notification_fmt_multi_msg_and_talker, 1,
//                    sessionCount, sessionUnread);
//        }
//
//        if (sessionUnread > 1) {
//            //一个人发了很多条
//            return context.getResources().getQuantityString(
//                    R.plurals.notification_fmt_multi_msg_and_one_talker, sessionUnread, sessionUnread);
//        }
        //一个人发了一条
        if (latestMessage instanceof TextMessage) {
            MentionedInfo mentionedInfo = latestMessage.getMentionedInfo();
            return ((TextMessage) latestMessage).getContent();
        } else if (latestMessage instanceof FileMessage) {
            if (((FileMessage) latestMessage).getType().equals(FileMessageType.GIF.toString())) {
                return mContext.getString(R.string.app_face);
            } else if (((FileMessage) latestMessage).getType().equals(FileMessageType.VIDEO.toString())) {
                return mContext.getString(R.string.app_video);
            }
            return mContext.getString(R.string.app_file);
        } else if (latestMessage instanceof VoiceMessage) {
            return mContext.getString(R.string.app_voice);
        } else if (latestMessage instanceof ImageMessage) {
            return mContext.getString(R.string.app_pic);
        } else if (latestMessage instanceof LocationMessage) {
            return mContext.getString(R.string.app_location);
        } else {
            return "";
        }

    }

    public String getGroupNotice(InformationNotificationMessage no) {
        String extra = no.getExtra();
        if (TextUtils.isEmpty(extra)) {
            extra = "";
        } else if (extra.equals(ShareLockManager.getInstance().getUserName())) {
            extra = "你";
        } else {
            ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(extra);
            if (info != null) {
                if (!TextUtils.isEmpty(info.getU_nickname())) {
                    extra = info.getU_nickname();
                }

            }
        }
        String noMessage = no.getMessage();
        if (!TextUtils.isEmpty(noMessage)) {
            return extra + noMessage;
        }
        return extra;
    }

    /**************************************
     * 单人聊天通知 start
     *************************************************************/
    //单聊消息的通知    私聊差显示的图像
    public final void showPrivateMessageNotification(final Message latestMessage) {
        if (latestMessage.getContent() instanceof CommandNotificationMessage) {
            //由于私聊里面有一个 删除好友的消息  不发通知
            return;
        }
        checkNotification();
        /**
         * 单聊
         * 1 标题是名字
         * 2 内容是最后的话
         * 3 提醒是名字加内容
         * */
        //获取联系人详情   没有下载
        ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(latestMessage.getTargetId());
        if (info != null) {
            //展示单人俩天的通知
            showContactNotifity(info, latestMessage);

        } else {
            //下载联系人后展示通知
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //下载联系人后展示通知
                    downContactShowNotifity(latestMessage);
                }
            });

        }

    }

    //没有联系人时 下载联系人并显示通知
    private void downContactShowNotifity(final Message latestMessage) {
        ContactInfoBiz.getContactInfo(latestMessage.getSenderUserId(), new GetContactInfoListener() {
            @Override
            public void getContactInfo(ContactInfo contactInfo) {
                if (contactInfo != null) {
                    showContactNotifity(contactInfo, latestMessage);
                }
            }
        });
    }

    //展示联系人消息通知
    private void showContactNotifity(ContactInfo info, Message latestMessage) {
        if (info == null) {
            return;
        }
        String name = info.getName();

        //提醒的文本
        String tickerText = getTickerText(name, latestMessage.getContent());
        //通知的内容
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SString.start_from, SString.notify_chat);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String contentText = getContentText(latestMessage.getContent());
        String url = info.getU_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        //显示通知
        showNotifyWithBigImg(latestMessage.getMessageId(), url, tickerText, name, contentText, pendingIntent);

    }
    /************************************** 单人聊天通知  end *************************************************************/

    /**
     * 展示带图像的通知
     *
     * @param id
     * @param url
     * @param tickerText
     * @param contentTitle
     * @param contentText
     * @param intent
     */
    private void showNotifyWithBigImg(final int id, final String url, final String tickerText,
                                      final String contentTitle, final String contentText,
                                      final PendingIntent intent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext).load(url)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>((int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 38, mContext.getResources().getDisplayMetrics()),
                                (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 38, mContext.getResources().getDisplayMetrics())) {
                            @Override

                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                                Notification notification = NotificationUtils.buildNotification(SApplication.getInstance(),
                                        R.mipmap.ic_launcher,
                                        tickerText, contentTitle, contentText,
                                        resource, intent);
                                ids.add(id);
                                mNotificationManager.notify(id, notification);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                Notification notification = NotificationUtils.buildNotification(SApplication.getInstance(),
                                        R.mipmap.ic_launcher,
                                        tickerText, contentTitle, contentText,
                                        null, intent);
                                ids.add(id);
                                mNotificationManager.notify(id, notification);
                            }
                        });
            }
        });

    }

    /**************************************
     * 群组聊天通知  start
     *************************************************************/
    //群组消息的通知  群聊差显示的大图
    public final void showGroupMessageNotification(final Message latestMessage) {
        if (latestMessage.getContent() instanceof InformationNotificationMessage) {
            //由于群聊里面的特殊消息  不发通知
            return;
        }
        checkNotification();
        /**
         * 群聊
         * 1 标题是群名
         * 2 内容  发送者+最后的话   注意：此中包含群组中的通知
         * 3 提醒是  群名+ 加内容
         * */

        String name = latestMessage.getTargetId();
        //获取群名   通知的内容标题show
        final GroupInfo info = GroupInfoSqlManager.getGroupInfo(ShareLockManager.subGroupId(name));
        if (info != null) {
            //展示群组消息通知
            showGroupNotifity(info, latestMessage);

        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    downGroupShowNotifity(latestMessage);
                }
            });

        }
    }

    //没有群组时 //下载群组并显示通知
    private void downGroupShowNotifity(final Message latestMessage) {
        GroupInfoBiz.getGroupInfo(latestMessage.getTargetId(), new GetGroupInfoListener() {
            @Override
            public void getGroupInfo(GroupInfo groupInfo) {
                if (groupInfo != null) {
                    showGroupNotifity(groupInfo, latestMessage);
                }
            }
        });
    }

    //展示群组消息通知
    private void showGroupNotifity(GroupInfo info, Message latestMessage) {
        if (info == null) {
            return;
        }
        MessageContent messageContent = latestMessage.getContent();
        //提醒的文本
        String tickerText = getTickerText(info.getG_name(), messageContent);
        //通知的内容

        //群聊天中的 通知相关
//        if (latestMessage.getContent() instanceof InformationNotificationMessage) {
//            //由于群聊里面的特殊消息  不发通知  此操作暂无作用
//            contentText = getGroupNotice((InformationNotificationMessage) latestMessage.getContent());
//        } else {
        ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(latestMessage.getSenderUserId());
        if (contactInfo == null) {
            //下载联系人
            ContactInfoBiz.getContactInfo(latestMessage.getSenderUserId(), null);
        }
        String contentText = (contactInfo != null ? contactInfo.getName() : latestMessage.getSenderUserId()) + " : " + getContentText(latestMessage.getContent());
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SString.start_from, SString.notify_chat);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //群组图像
        String url = info.getG_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        //检查是否at我
        if (isAtMe(messageContent)) {
            tickerText = mContext.getString(R.string.someone_at_me) + tickerText;
            contentText = mContext.getString(R.string.someone_at_me) + contentText;
        }
        //显示通知
        showNotifyWithBigImg(latestMessage.getMessageId(), url, tickerText, info.getG_name(), contentText, pendingIntent);

    }
    /**************************************     * 群组聊天通知  end     *************************************************************/


    /**************************************
     * 系统消息通知  start
     *************************************************************/

    //系统消息的通知
    public final void showSysMessageNotification(Message latestMessage) {
        checkNotification();
        /**
         * 系统消息
         *  分为 添加好友  群组   系统
         *  1 添加好友
         *  2 群组
         *  3 系统
         *
         * 1 标题是  根据类型划分
         * 2 内容     根据类型划分
         * 3 提醒是  根据类型划分
         * */
        String targetId = latestMessage.getTargetId();
        String contentText = null;
        String contentTitle = null;
        try {
            if (targetId.startsWith(SString.FIREND_ACTION_)) {
                //处理添加好友的通知
                contentTitle = mContext.getString(R.string.friend_operate_notice);
                contentText = doFriendAction_((TextMessage) latestMessage.getContent());//发送者   目前是： FirendAction_1


            } else if (targetId.startsWith(SString.GROUP_INFO_)) {
                // 处理群组 解散 踢出
                contentTitle = mContext.getString(R.string.group_about_info_notice);

                contentText = doGroupInfo_((TextMessage) latestMessage.getContent());


            } else if (targetId.startsWith(SString.GROUP_ACTION_)) {
                // 邀请 及邀请的回复
                contentTitle = mContext.getString(R.string.group_operate_notice);
                contentText = doGroupAction_(mContext, (TextMessage) latestMessage.getContent());

            } else if (targetId.startsWith(SString.MANAGER_GROUP_ACTION_)) {
                //申请加入群组
                contentTitle = mContext.getString(R.string.group_manage_operate);

                contentText = doManagerGroupAction_(mContext, (TextMessage) latestMessage.getContent());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取群名   通知的内容标题
        //提醒的文本

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SString.start_from, SString.notify_chat);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = NotificationUtils.buildNotification(mContext, R.mipmap.ic_launcher,
                contentText, contentTitle, contentText, null, pendingIntent);
        ids.add(latestMessage.getMessageId());
        mNotificationManager.notify(latestMessage.getMessageId(), notification);
    }

    //处理添加好友
    private String doFriendAction_(TextMessage message) throws Exception {
        JSONObject object = new JSONObject(message.getContent());
        String operation = object.getString(SString.OPERATION);
        String sourceUserId = object.getString("sourceUserId");// 发送人
        ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(sourceUserId);
        if (contactInfo != null) {
            sourceUserId = contactInfo.getName();
        }
        if (!TextUtils.isEmpty(operation)) {
            if (operation.equals(SString.ADD_FRIEND_OPERATION)) {
                //是  添加好友请求的操作

                return mContext.getString(R.string.add_friend_request, sourceUserId);

            } else if (operation.equals(SString.FRIENDS_ADD_REQUEST_REPLY)) {
                // 回复请求的操作   有两种状态
                String status = object.getString("status");


                if (status.equals("DISAGREE")) {

                    return mContext.getString(R.string.reject_friend_request, sourceUserId);


                } else if (status.equals("AGREE")) {
                    return mContext.getString(R.string.agree_friend_request, sourceUserId);

                }
            }
        }
        return "";

    }

    // 处理群组 解散 踢出
    private String doGroupInfo_(TextMessage message) throws Exception {
        JSONObject object = new JSONObject(message.getContent());
        String operation = object.getString(SString.OPERATION);

        if (!TextUtils.isEmpty(operation) && operation.equals(SString.DISMISS_GROUP)) {
            // 解散群组
            String groupId = object.getString("groupId");
            if (object.has("groupName")) {
                groupId = object.getString("groupName");
            } else {
                GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
                if (groupInfo != null) {
                    groupId = groupInfo.getG_name();
                }
            }
            String operator = object.getString("operator");// 发送人
            ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(operation);
            if (contactInfo != null) {
                operator = contactInfo.getName();
            }
            // xx 解散了群组  xxx;
            return mContext.getString(R.string.group_dis_tip, operator, groupId);

        }
        if (!TextUtils.isEmpty(operation) && operation.equals(SString.KICK_MESSAGE)) {
            // 被踢出群组
            String groupId = object.getString("groupId");
//            String groupName = object.getString("groupName");
            GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
            if (groupInfo != null) {
                groupId = groupInfo.getG_name();
            }
            return mContext.getString(R.string.group_member_kick, groupId);
        }
        return "";

    }


    // 群组 邀请新成员     // 邀请 及邀请的回复
    private String doGroupAction_(Context context, TextMessage message) throws Exception {
        JSONObject object = new JSONObject(message.getContent());
        String operation = object.getString(SString.OPERATION);
        if (!TextUtils.isEmpty(operation)) {
            if (operation.equals(SString.INVITATION_USER_ADD_GROUP)) {
                //收到邀请加入群组
                String groupId = object.getString("groupId");
                String groupName = object.getString("groupName");
                String sourceUserId = object.getString("sourceUserId");// 发送人
                ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(sourceUserId);
                if (contactInfo != null) {
                    sourceUserId = contactInfo.getName();
                }
                return context.getString(R.string.invite_join_group, sourceUserId, groupName);

            } else if (operation.equals(SString.INVITATION_USER_ADD_GROUP_REPLY)) {
                //收到 回复
                String status = object.getString("status");
                String sourceUserId = object.getString("sourceUserId");// 发送人
//                String groupId = object.getString("groupId");
                String groupName = object.getString("groupName");
                ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(sourceUserId);
                if (contactInfo != null) {
                    sourceUserId = contactInfo.getName();
                }
                if (status.equals("AGREE")) {

                    return context.getString(R.string.invite_join_group_reply_a, sourceUserId, groupName);
                } else if (status.equals("DISAGREE")) {

                    return context.getString(R.string.invite_join_group_reply_d, sourceUserId, groupName);

                }
            }
        }
        return "";
    }

    //申请加入群组  及回复
    private String doManagerGroupAction_(Context context, TextMessage message) throws Exception {
        JSONObject object = new JSONObject(message.getContent());
        String operation = object.getString(SString.OPERATION);
        if (!TextUtils.isEmpty(operation)) {
            if (operation.equals(SString.APPLY_ADD_GROUP_REQUEST)) {
                //申请加入群组
                String sourceUserId = object.getString("sourceUserId");// 发送人
                String groupName = object.getString("groupName");
                ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(operation);
                if (contactInfo != null) {
                    sourceUserId = contactInfo.getName();
                }
                return context.getString(R.string.apply_join_group_, sourceUserId, groupName);

            } else if (operation.equals(SString.APPLY_ADD_GROUP_REQUEST_REPLY)) {
                //回复申请
                String sourceUserId = object.getString("sourceUserId");// 发送人
                String groupName = object.getString("groupName");
                String status = object.getString("status");
                ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(operation);
                if (contactInfo != null) {
                    sourceUserId = contactInfo.getName();
                }
                if (status.equals("AGREE")) {
                    return context.getString(R.string.invite_join_group_reply_a, sourceUserId, groupName);
                } else if (status.equals("DISAGREE")) {
                    return context.getString(R.string.invite_join_group_reply_d, sourceUserId, groupName);
                }
            }
        }

        return "";

    }

    /**************************************
     * 系统消息通知  end
     *************************************************************/
    /**************************************
     * 踢出通知  start
     *************************************************************/
    public final void showKickoffNotification(String kickofftext) {
        checkNotification();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(SString.start_from, SString.notify_chat);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = NotificationUtils.buildNotification(mContext, R.mipmap.ic_launcher,
                kickofftext, "踢出通知", kickofftext, null, pendingIntent);
        ids.add(Integer.MAX_VALUE);
        mNotificationManager.notify(Integer.MAX_VALUE, notification);
    }

    /**************************************
     * 踢出通知  end
     *************************************************************/


    private void checkNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

    }

    private ArrayList<Integer> ids = new ArrayList<>();

    public static void cancelCCPNotification(int id) {
        getInstance().checkNotification();
        mNotificationManager.cancel(id);
    }

    public void cancelCCPNotificationAll() {
        getInstance().checkNotification();
        int size = ids.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mNotificationManager.cancel(ids.get(i));
            }
        }

    }

    private boolean isAtMe(MessageContent messageContent) {
        // 包含at 内容
        MentionedInfo mentionedInfo = messageContent.getMentionedInfo();
        if (mentionedInfo != null) {
            if (mentionedInfo.getType() == MentionedInfo.MentionedType.PART) {
                List<String> userIdList = mentionedInfo.getMentionedUserIdList();
                if (userIdList != null && userIdList.size() > 0) {
                    int size = userIdList.size();
                    for (int i = 0; i < size; i++) {
                        String userId = userIdList.get(i);
                        if (!TextUtils.isEmpty(userId)) {
                            if (userId.equals(ShareLockManager.getInstance().getUserName())) {
                                return true;
                            }
                        }
                    }

                }

            } else if (mentionedInfo.getType() == MentionedInfo.MentionedType.ALL) {
                return true;
            }
        }
        return false;
    }
}