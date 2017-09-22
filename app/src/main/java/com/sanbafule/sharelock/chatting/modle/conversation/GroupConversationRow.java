package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupHolder;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.MyString;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 群聊
 */
public class GroupConversationRow extends BaseConversationRow {
    public GroupConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {
        if (holder instanceof GroupHolder) {
            String senderUserId = conversation.getSenderUserId();
            final String targetId = conversation.getTargetId();
            MessageContent latestMessage = conversation.getLatestMessage();
            String groupName = null;
            String groupMemberName = null;
            // 发消息
            if (MyString.hasData(senderUserId) && senderUserId.equals(ShareLockManager.getInstance().getUserName())) {

                // 设置时间
                ((GroupHolder) holder).setSendDate(conversation);
                // 设置发送状态
                ((GroupHolder) holder).setMessageSentStatus(((GroupHolder) holder).mConversationSentStatus, conversation);

                // 设置群成员的名字为 我
                ((GroupHolder) holder).mGroupMember.setText("我:");
            } else {
                if (conversation.getMentionedCount() > 0) {
                    ((GroupHolder) holder).atMe.setVisibility(View.VISIBLE);
                }
                groupMemberName = senderUserId;
                // 设置群组成员的姓名
                ((GroupHolder) holder).setGroupMemberInfo(((GroupHolder) holder).mGroupMember, groupMemberName);
                ((GroupHolder) holder).setReceiveDate(conversation);
            }
            groupName = targetId;
            ((GroupHolder) holder).setGroupInfo(context, groupName);
            // 设置消息类型
            ((GroupHolder) holder).setMessageType(latestMessage,null ,((GroupHolder) holder).bodyTextView);

            ((GroupHolder) holder).setBase(conversation, context, holder.getLayoutPosition(),targetId,dragListener);

            ((GroupHolder) holder).onClick(((GroupHolder) holder).readTextView, context, holder.getLayoutPosition(),targetId);
            //  跳转聊天页面
            ((GroupHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupHolder) holder).clearUnReadMessage(context,holder.getLayoutPosition(),((GroupHolder) holder).readTextView,targetId);
                    ShareLockManager.startGroupChattingActivity(context, targetId, targetId, false);
//                    SChattingHelp.startChattingAction(finalName, (Activity) context);
                }
            });
        }

    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_group_conversion, viewGroup, false);
    }

    @Override
    public int getConversationViewType() {

        return ConversationRowType.GROUP.getId();
    }
}
