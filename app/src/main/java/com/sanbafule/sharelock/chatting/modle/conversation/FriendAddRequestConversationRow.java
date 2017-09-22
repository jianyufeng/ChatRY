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
import com.sanbafule.sharelock.chatting.modle.conversation.holder.FriendAddRequestHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 好友添加的回执
 */
public class FriendAddRequestConversationRow extends BaseConversationRow {
    public FriendAddRequestConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {
        if (holder instanceof FriendAddRequestHolder) {

            ((FriendAddRequestHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((FriendAddRequestHolder) holder).setReceiveDate(conversation);

            ((FriendAddRequestHolder) holder).setBase(conversation,context,holder.getLayoutPosition(),conversation.getSenderUserId(),dragListener);

            ((FriendAddRequestHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FriendAddRequestHolder) holder).clearUnReadMessage(context,holder.getLayoutPosition(),((FriendAddRequestHolder) holder).readTextView,conversation.getSenderUserId());
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
                    String status = object.getString("status");
                    if (status.equals("DISAGREE")) {
                        ((FriendAddRequestHolder) holder).setConversationBody(sourceUserId,context.getString(R.string.reject_friend_request));
//                        ((FriendAddRequestHolder) holder).nameTextView.setText("好友拒绝");
                    }else  if(status.equals("AGREE")){
                        ((FriendAddRequestHolder) holder).setConversationBody(sourceUserId,context.getString(R.string.agree_friend_request));
//                        ((FriendAddRequestHolder) holder).nameTextView.setText("好友同意");
                    }
                    ((FriendAddRequestHolder) holder).nameTextView.setText(context.getString(R.string.friend_operate_notice));
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
        return ConversationRowType.FRIENDS_ADD_REQUEST_REPLY.getId();
    }
}
