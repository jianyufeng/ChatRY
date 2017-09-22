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
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupMemberAddGroupHolder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 群成员主动进群 (发送给管理员的通知)
 */
public class GroupMemberAddGroupConversationRow   extends  BaseConversationRow{
    public GroupMemberAddGroupConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {

        if (holder instanceof GroupMemberAddGroupHolder){

            ((GroupMemberAddGroupHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((GroupMemberAddGroupHolder) holder).nameTextView.setText(context.getString(R.string.group_manage_operate));
            ((GroupMemberAddGroupHolder) holder).setReceiveDate(conversation);

            ((GroupMemberAddGroupHolder) holder).setBase(conversation, context, holder.getLayoutPosition(),conversation.getSenderUserId(),dragListener);

            ((GroupMemberAddGroupHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupMemberAddGroupHolder) holder).clearUnReadMessage(context,holder.getLayoutPosition(),((GroupMemberAddGroupHolder) holder).readTextView,conversation.getSenderUserId());

                    context.startActivity(new Intent(context, RequestJoinActivity.class));
                }
            });
            String content = ((TextMessage) conversation.getLatestMessage()).getContent();
            JSONObject object= null;
            String s="[ %s ]申请加入群组 %s";
            try {
                object = new JSONObject(content);
                String sourceUserId=object.getString("sourceUserId");// 发送人
                String groupName=object.getString("groupName");
             ((GroupMemberAddGroupHolder) holder).setGroupBody(sourceUserId,groupName,s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_friend_add_conversion,viewGroup,false);
    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.APPLY_ADD_GROUP_REQUEST.getId();
    }
}
