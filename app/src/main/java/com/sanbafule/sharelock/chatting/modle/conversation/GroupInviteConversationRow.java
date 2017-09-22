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
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupInviteHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 邀请进群的通知 （邀请人（用户）接收）
 */
public class GroupInviteConversationRow extends BaseConversationRow {
    public GroupInviteConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position, RoundNumberDragListener dragListener) {
        if (holder instanceof GroupInviteHolder) {

            ((GroupInviteHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((GroupInviteHolder) holder).nameTextView.setText(context.getString(R.string.group_operate_notice));
            ((GroupInviteHolder) holder).setReceiveDate(conversation);

            ((GroupInviteHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), conversation.getSenderUserId(), dragListener);

            ((GroupInviteHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupInviteHolder) holder).clearUnReadMessage(context, holder.getLayoutPosition(), ((GroupInviteHolder) holder).readTextView, conversation.getSenderUserId());

                    // 跳转群组列表页面
                    context.startActivity(new Intent(context, InviteJoinActivity.class));
                }
            });
            String content = ((TextMessage) conversation.getLatestMessage()).getContent();
//           {"operation":"INVITATION_USER_ADD_GROUP","groupName":"查房","nowStatus":0,"requestId":"24","targetUserId":"xcy001","sourceUserId":"zhouliwen22"}
            try {
                JSONObject object = new JSONObject(content);
                String sourceUserId = object.getString("sourceUserId");// 发送人
                String groupName = object.getString("groupName");
                ((GroupInviteHolder) holder).setGroupBody(sourceUserId, groupName, context.getString(R.string.invite_join_group));
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
        return ConversationRowType.GROUP_INVITE.getId();
    }
}
