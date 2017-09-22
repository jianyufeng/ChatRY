package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.contact.NewFriendListActivity;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.FriendAddHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/20 16:16
 * cd : 三八妇乐
 * 描述：  好友添加的会话
 */
public class FriendAddConversationRow extends BaseConversationRow {
    public FriendAddConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {
        if (holder instanceof FriendAddHolder) {
            ((FriendAddHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((FriendAddHolder) holder).nameTextView.setText(context.getString(R.string.friend_operate_notice));
            ((FriendAddHolder) holder).setReceiveDate(conversation);

            ((FriendAddHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), conversation.getSenderUserId(),dragListener);

            ((FriendAddHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FriendAddHolder) holder).clearUnReadMessage(context,holder.getLayoutPosition(),((FriendAddHolder) holder).readTextView,conversation.getSenderUserId());
                    // 跳转新朋友列表页面
                    context.startActivity(new Intent(context, NewFriendListActivity.class));
                }
            });

            MessageContent latestMessage = conversation.getLatestMessage();
            if (latestMessage instanceof TextMessage) {
                final String content = ((TextMessage) latestMessage).getContent();
                try {
                    JSONObject object = new JSONObject(content);
                    String sourceUserId = object.getString("sourceUserId");// 发送人
                    // 设置会话的的body
                    ((FriendAddHolder) holder).setConversationBody( sourceUserId, context.getString(R.string.add_friend_request));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_friend_add_conversion, viewGroup, false);
    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.FRIEND_ADD.getId();
    }
}
