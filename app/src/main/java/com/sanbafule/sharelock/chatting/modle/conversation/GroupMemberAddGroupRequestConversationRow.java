package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.groupNotice.RequestJoinActivity;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupMemberAddGroupRequestHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 群成群主动加群的回执 (管理员发送给用户的通知)
 */
public class GroupMemberAddGroupRequestConversationRow extends BaseConversationRow {
    public GroupMemberAddGroupRequestConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position, RoundNumberDragListener dragListener) {

        if (holder instanceof GroupMemberAddGroupRequestHolder) {
            ((GroupMemberAddGroupRequestHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((GroupMemberAddGroupRequestHolder) holder).nameTextView.setText(context.getString(R.string.group_manage_operate));
            ((GroupMemberAddGroupRequestHolder) holder).setReceiveDate(conversation);

            ((GroupMemberAddGroupRequestHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), conversation.getSenderUserId(), dragListener);

            ((GroupMemberAddGroupRequestHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupMemberAddGroupRequestHolder) holder).clearUnReadMessage(context, holder.getLayoutPosition(), ((GroupMemberAddGroupRequestHolder) holder).readTextView, conversation.getSenderUserId());

                    context.startActivity(new Intent(context, RequestJoinActivity.class));
                }
            });
            String content = ((TextMessage) conversation.getLatestMessage()).getContent();
//            String agree = "[ %s ] 同意您加入群组 %s";
//            String reject = "[ %s ] 拒绝您加入群组 %s";
            try {
                JSONObject object = new JSONObject(content);
                String sourceUserId = object.getString("sourceUserId");// 发送人
                String groupName = object.getString("groupName");
                String status = object.getString("status");
                if (status.equals("AGREE")) {
                    ((GroupMemberAddGroupRequestHolder) holder).setGroupBody(sourceUserId, groupName, context.getString(R.string.apply_join_group_reply_a));
                } else if (status.equals("DISAGREE")) {
                    ((GroupMemberAddGroupRequestHolder) holder).setGroupBody(sourceUserId, groupName, context.getString(R.string.apply_join_group_reply_d));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_friend_add_conversion, viewGroup, false);
    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.APPLY_ADD_GROUP_REQUEST_REPLY.getId();
    }
}
