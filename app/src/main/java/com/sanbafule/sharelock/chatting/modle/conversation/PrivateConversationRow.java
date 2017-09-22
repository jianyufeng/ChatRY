package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.PrivateHolder;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.MyString;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 私聊
 */
public class PrivateConversationRow extends BaseConversationRow {

    public PrivateConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation,  int position,RoundNumberDragListener dragListener) {
        if (holder instanceof PrivateHolder) {
            String senduserId = conversation.getSenderUserId();
            final String targetId = conversation.getTargetId();
            MessageContent latestMessage = conversation.getLatestMessage();
            String name = null;
            // 发消息
            if (MyString.hasData(senduserId) && senduserId.equals(ShareLockManager.getInstance().getUserName())) {
                name = targetId;
                // 设置时间
                ((PrivateHolder) holder).setSendDate(conversation);
                // 设置发送状态
                ((PrivateHolder) holder).setMessageSentStatus(((PrivateHolder) holder).mConversationSentStatus, conversation);

            } else {
                name = senduserId;
                ((PrivateHolder) holder).setReceiveDate(conversation);
            }
            // 设置联系人信息
            ((PrivateHolder) holder).setContactInfo(context, name);
            // 设置消息类型
            ((PrivateHolder) holder).setMessageType(latestMessage,name ,((PrivateHolder) holder).bodyTextView);

            ((PrivateHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), name,dragListener);

            ((PrivateHolder) holder).onClick(((PrivateHolder) holder).readTextView, context, holder.getLayoutPosition(), name);



            //  跳转聊天页面
            ((PrivateHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PrivateHolder) holder).clearUnReadMessage(context, holder.getLayoutPosition(), ((PrivateHolder) holder).readTextView, targetId);
                    ShareLockManager.startChattingActivity(context, targetId, null, false);
                }
            });

        }

    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_normal_conversion, viewGroup, false);

    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.PRIVATE.getId();

    }

}
