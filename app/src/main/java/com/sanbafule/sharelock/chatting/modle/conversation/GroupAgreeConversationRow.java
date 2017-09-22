package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.groupNotice.InviteJoinActivity;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupAgreeHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述：  同意添加群组
 */
public class GroupAgreeConversationRow extends BaseConversationRow {
    public GroupAgreeConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position, RoundNumberDragListener dragListener) {

        if (holder instanceof GroupAgreeHolder) {
            ((GroupAgreeHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((GroupAgreeHolder) holder).nameTextView.setText(context.getString(R.string.group_operate_notice));
            ((GroupAgreeHolder) holder).setReceiveDate(conversation);

            ((GroupAgreeHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), conversation.getSenderUserId(), dragListener);

            ((GroupAgreeHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupAgreeHolder) holder).clearUnReadMessage(context, holder.getLayoutPosition(), ((GroupAgreeHolder) holder).readTextView, conversation.getSenderUserId());
                    context.startActivity(new Intent(context, InviteJoinActivity.class));
                }
            });
            MessageContent latestMessage = conversation.getLatestMessage();
            String agree = "[ %s ] 同意了您的邀请";
            String reject = "[ %s ] 拒绝了您的邀请";
            if (latestMessage instanceof TextMessage) {
                JSONObject object = null;
                try {
                    object = new JSONObject(((TextMessage) latestMessage).getContent());
                    String sourceUserId = object.getString("sourceUserId");// 发送人
                    String groupName = object.getString("groupName");
                    String status = object.getString("status");
                    if (status.equals("AGREE")) {
                        ((GroupAgreeHolder) holder).setConversationBody(sourceUserId, agree);
                    } else if (status.equals("DISAGREE")) {
                        ((GroupAgreeHolder) holder).setConversationBody(sourceUserId, reject);
                    }
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
        return ConversationRowType.GROUP_AGREE.getId();
    }
}
