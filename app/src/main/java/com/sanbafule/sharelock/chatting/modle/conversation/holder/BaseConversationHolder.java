package com.sanbafule.sharelock.chatting.modle.conversation.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.GetGroupInfoListener;
import com.sanbafule.sharelock.UI.fragment.ConversationFragment;
import com.sanbafule.sharelock.adapter.ConversationAdapter;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupInfoBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.util.DateUtils;
import com.sanbafule.sharelock.util.SimpleCommonUtils;
import com.sanbafule.sharelock.view.RoundNumber;
import com.sanbafule.sharelock.view.SwipeLayout;

import io.rong.imlib.model.Conversation;
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
 * Administrator
 * 作者:Created by ShareLock on 2016/12/20 10:19
 * cd : 三八妇乐
 * 描述：
 */
public abstract class BaseConversationHolder extends RecyclerView.ViewHolder {


    public View baseView;
    // 头像
    public ImageView headImageView;
    // 名称
    public TextView nameTextView;
    // 置顶
    public TextView topTextView;
    // 已读
    public TextView readTextView;
    //删除
    public TextView deleteTextView;
    // 消息
    public TextView bodyTextView;
    // 时间
    public TextView dateTextView;
    // 消息未读数
    public RoundNumber number;
    // 点击的item
    public RelativeLayout conversationItem;
    // sl
    public SwipeLayout swipeLayout;

    public BaseConversationHolder(View itemView) {
        super(itemView);
        this.baseView = itemView;
        initBaseView(baseView);
    }

    private void initBaseView(View baseView) {
        headImageView = (ImageView) baseView.findViewById(R.id.conversion_img);
        nameTextView = (TextView) baseView.findViewById(R.id.conversion_name);
        topTextView = (TextView) baseView.findViewById(R.id.top);
        readTextView = (TextView) baseView.findViewById(R.id.read);
        deleteTextView = (TextView) baseView.findViewById(R.id.delete);
        bodyTextView = (TextView) baseView.findViewById(R.id.conversion_body);
        dateTextView = (TextView) baseView.findViewById(R.id.conversion_time);
        number = (RoundNumber) baseView.findViewById(R.id.conversion_unread);
        conversationItem = (RelativeLayout) baseView.findViewById(R.id.conversion_item);
        swipeLayout = (SwipeLayout) baseView.findViewById(R.id.sl);
    }


    public ImageView getHeradImageView() {
        return headImageView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getTopTextView() {
        return topTextView;
    }

    public TextView getDeleteTextView() {
        return deleteTextView;
    }

    public TextView getBodyTextView() {
        return bodyTextView;
    }

    public TextView getDateTextView() {
        return dateTextView;
    }

    public RoundNumber getNumber() {
        return number;
    }

    public RelativeLayout getConversationItem() {
        return conversationItem;
    }


    public void setBase(final Conversation conversation, Context context, final int position, final String name, final RoundNumberDragListener dragListener) {
        setConvetsionMenuState(conversation, context);
        TopAndDeleteClick(context, position, name);

        setOnSwipeListener(context);

        setUnreadCount(conversation.getUnreadMessageCount());
        number.setClickListener(new RoundNumber.ClickListener() {
            @Override
            public void onDown() {
                dragListener.onDown(number);
            }

            @Override
            public void onMove(float curX, float curY) {
                dragListener.onMove(curX, curY);
            }

            @Override
            public void onUp() {
                dragListener.onUp(conversation, name);
            }
        });
    }

    private void setConvetsionMenuState(Conversation conversation, Context context) {
        boolean top = conversation.isTop();
        //标记置顶的状态
        getConversationItem().setBackgroundColor(top ? 0x55000000 : 0xffffffff);
        //设置 文本
        getTopTextView().setText(top ? context.getString(R.string.cancel_top_conversion) : context.getString(R.string.top_conversion));

        readTextView.setVisibility((conversation.getConversationType() == Conversation.ConversationType.PRIVATE ||
                conversation.getConversationType() == Conversation.ConversationType.GROUP) ?
                View.VISIBLE : View.GONE);


    }

    /**
     * 设置未读数
     *
     * @param unRead
     */
    public void setUnreadCount(int unRead) {
        if (unRead <= 0) {
            number.setVisibility(View.GONE);
        }
        if (unRead > 0 && unRead < 99) {
            number.setVisibility(View.VISIBLE);
            number.setMessage(String.valueOf(unRead));
        }
        if (unRead > 99) {
            number.setVisibility(View.VISIBLE);
            number.setMessage(String.valueOf(99));
        }
    }

    /**
     * 显示消息类型
     *
     * @param latestMessage
     * @param mConversationBody
     */
    public void setMessageType(MessageContent latestMessage, String name, TextView mConversationBody) {
        mConversationBody.setTextColor(Color.GRAY);
        if (latestMessage instanceof TextMessage) {
            SimpleCommonUtils.spannableEmoticonFilter(mConversationBody, ((TextMessage) latestMessage).getContent());
        } else if (latestMessage instanceof ImageMessage) {
            mConversationBody.setText(SApplication.getInstance().getString(R.string.app_pic));
        } else if (latestMessage instanceof VoiceMessage) {
            mConversationBody.setText(SApplication.getInstance().getString(R.string.app_voice));
        } else if (latestMessage instanceof FileMessage) {
            if (((FileMessage) latestMessage).getType().equals(FileMessageType.GIF.toString())) {
                mConversationBody.setText(SApplication.getInstance().getString(R.string.app_face));
            }else if (((FileMessage) latestMessage).getType().equals(FileMessageType.VIDEO.toString())) {
                mConversationBody.setText(SApplication.getInstance().getString(R.string.app_video));
            } else {
                mConversationBody.setText(SApplication.getInstance().getString(R.string.app_file));
            }

        } else if (latestMessage instanceof LocationMessage) {
            mConversationBody.setText(SApplication.getInstance().getString(R.string.app_location));
        } else if (latestMessage instanceof InformationNotificationMessage) {
            InformationNotificationMessage no = (InformationNotificationMessage) latestMessage;
            String extra = no.getExtra();
            if (TextUtils.isEmpty(extra)) {
                extra = "";
            } else if (extra.equals(ShareLockManager.getInstance().getUserName())) {
                extra = "你";
            } else {
                ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(extra);
                if (info != null) {
                    extra = info.getName();
                }
            }
            String msg = no.getMessage();
            if (!TextUtils.isEmpty(msg)) {
                mConversationBody.setText(extra + msg);
            }
            mConversationBody.setTextColor(Color.GREEN);
        } else if (latestMessage instanceof CommandNotificationMessage) {
            // 显示昵称
            if (!TextUtils.isEmpty(name)) {
                ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(name);
                if (info != null) {
                    name = info.getName();
                }
            }
            mConversationBody.setText(SApplication.getInstance().getString(R.string.friend_del_msg, name));
        }
    }


    /**
     * 设置发送时间
     *
     * @param conversation
     */
    public void setSendDate(Conversation conversation) {
        if (conversation.getSentStatus() == Message.SentStatus.SENDING) {
            dateTextView.setText(ShareLockManager.getInstance().getContext().getString(R.string.conv_msg_sending));
        } else if (conversation.getSentStatus() == Message.SentStatus.FAILED) {
            dateTextView.setText(ShareLockManager.getInstance().getContext().getString(R.string.conv_msg_failed));
        } else {
            dateTextView.setText(DateUtils.getDateString(conversation.getSentTime(), DateUtils.SHOW_TYPE_CALL_LOG));
        }

    }

    /**
     * 设置接受时间
     *
     * @param conversation
     */
    public void setReceiveDate(Conversation conversation) {
        dateTextView.setText(DateUtils.getDateString(conversation.getReceivedTime(), DateUtils.SHOW_TYPE_CALL_LOG));
    }

    /**
     * 设置消息发送状态
     *
     * @param mConversationSentStatus
     * @param conversation
     */
    public void setMessageSentStatus(ImageView mConversationSentStatus, Conversation conversation) {
        switch (conversation.getSentStatus()) {
            case SENDING:
                mConversationSentStatus.setVisibility(View.VISIBLE);
                mConversationSentStatus.setImageResource(R.drawable.msg_state_sending);
                break;
            case SENT:
                mConversationSentStatus.setVisibility(View.GONE);
                break;
            case FAILED:
                mConversationSentStatus.setVisibility(View.VISIBLE);
                mConversationSentStatus.setImageResource(R.drawable.msg_state_failed);
                break;
            case RECEIVED:
                break;
            case READ:
                break;
            case DESTROYED:
                break;

        }

    }


    /**
     * 置顶删除操作
     *
     * @param context
     * @param position
     */
    public void TopAndDeleteClick(final Context context, final int position, String name) {
        onClick(deleteTextView, context, position, name);
        onClick(topTextView, context, position, name);
    }

    /**
     * 点击操作
     *
     * @param view
     * @param context
     * @param position
     */
    public void onClick(View view, final Context context, final int position, final String name) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = ((MainActivity) context).getNavigateTabBar().getFragment("好友");
                if (fragment instanceof ConversationFragment) {
                    ((ConversationFragment) fragment).onItemClick(v, position, number, name);
                }
            }
        });
    }

    /**
     * 设置联系人的信息
     *
     * @param context
     */
    public void setContactInfo(final Context context, String name) {

        final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(name);
        if (info != null) {
            setContactInfo(context, info);
        } else {
            ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {
                    setContactInfo(context, contactInfo);
                }
            });
        }
    }

    public void setContactInfo(Context context, ContactInfo contactInfo) {
        nameTextView.setText(contactInfo.getName());
        ImageBiz.showImage(context, headImageView, ShareLockManager.getImgUrl(contactInfo.getU_header()), R.drawable.icon_touxiang_persion_gray);

    }

    /**
     * 设置群组信息
     *
     * @param context
     */

    public void setGroupInfo(final Context context, String groupName) {

        int i = groupName.lastIndexOf("_") + 1;

        String substring = groupName.substring(i);
        GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(substring);
        if (groupInfo != null) {
            setGroupInfo(context, groupInfo);
        } else {
            GroupInfoBiz.getGroupInfo(substring, new GetGroupInfoListener() {
                @Override
                public void getGroupInfo(GroupInfo groupInfo) {
                    setGroupInfo(context, groupInfo);
                }
            });
        }


    }

    public void setGroupInfo(Context context, GroupInfo groupInfo) {
        nameTextView.setText(groupInfo.getG_name());
        ImageBiz.showImage(context, headImageView, ShareLockManager.getImgUrl(groupInfo.getG_header()), R.drawable.icon_touxiang_persion_gray);
    }

    /**
     * 设置群组的群成员的名字
     *
     * @param mTextView
     */
    public void setGroupMemberInfo(final TextView mTextView, String groupMemberName) {
        final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(groupMemberName);
        if (info != null) {
            mTextView.setText(info.getName() + ":");
        } else {
            ContactInfoBiz.getContactInfo(groupMemberName, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {
                    mTextView.setText(contactInfo.getName() + ":");
                }
            });
        }
    }


    /**
     * 设置会话的body体
     *
     * @param name
     * @param s
     */
    public void setConversationBody(String name, final String s) {
        final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(name);
        if (info != null) {
            bodyTextView.setText(String.format(s, info.getName()));
        } else {
            ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {
                    bodyTextView.setText(String.format(s, contactInfo.getName()));
                }
            });
        }
    }

    public void setGroupBody(String sourceUserId, final String groupName, final String s) {
        ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(sourceUserId);
        if (contactInfo != null) {
            String body = String.format(s, contactInfo.getName(), groupName);
            bodyTextView.setText(body);
        } else {
            ContactInfoBiz.getContactInfo(sourceUserId, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {
                    String body = String.format(s, contactInfo.getName(), groupName);
                    bodyTextView.setText(body);
                }
            });
        }
    }

    public void setGroupDisBody(String sourceUserId, String groupId, final String s) {
        String nackName = null;
        String groupName = null;
        ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(sourceUserId);
        if (contactInfo != null) {
            nackName = contactInfo.getU_nickname();
            if (TextUtils.isEmpty(nackName)) {
                nackName = contactInfo.getU_username();
            }

        } else {
            ContactInfoBiz.getContactInfo(sourceUserId, new GetContactInfoListener() {
                @Override
                public void getContactInfo(ContactInfo contactInfo) {

                }
            });
        }
        GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
        if (groupInfo != null) {
            groupName = groupInfo.getG_name();
        } else {
            final String finalNackName = nackName;
            GroupInfoBiz.getGroupInfo(groupId, new GetGroupInfoListener() {
                @Override
                public void getGroupInfo(GroupInfo groupInfo) {
                    String body = String.format(s, finalNackName, groupInfo.getG_name());
                    bodyTextView.setText(body);
                }
            });
        }
        String body = String.format(s, nackName, groupName);
        bodyTextView.setText(body);
    }

    public void setGroupKickBody(String groupId, final String s) {
        GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
        if (groupInfo != null) {
            String body = String.format(s, groupInfo.getG_name());
            bodyTextView.setText(body);
        } else {
            GroupInfoBiz.getGroupInfo(groupId, new GetGroupInfoListener() {
                @Override
                public void getGroupInfo(GroupInfo groupInfo) {
                    String body = String.format(s, groupInfo.getG_name());
                    bodyTextView.setText(body);
                }
            });
        }
    }

//    /**
//     * 下载并显示图片
//     *
//     * @param context
//     * @param url
//     * @param userName
//     * @param errorId
//     */
//    public void setImage(Context context, String url, String userName, int errorId) {
//        DownImgBiz biz = new DownImgBiz();
//        biz.downLoadHeardImg(context, headImageView, url, userName, errorId);
//    }

    /**
     * @param context
     */
    public void setOnSwipeListener(Context context) {
        final Fragment fragment = ((MainActivity) context).getNavigateTabBar().getFragment("好友");
        if (fragment instanceof ConversationFragment) {
            ConversationAdapter adapter = (ConversationAdapter) ((ConversationFragment) fragment).getAdapter();
            swipeLayout.setOnSwipeListener(adapter);
        }

    }

    public void clearUnReadMessage(Context context, int position, View v, String name) {
        final Fragment fragment = ((MainActivity) context).getNavigateTabBar().getFragment("好友");
        if (fragment instanceof ConversationFragment) {
            ((ConversationFragment) fragment).onItemClick(v, position, number, name);
        }
    }
}
