package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.group.GroupNotificationActivity;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.BaseConversationHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupDismissHolder;
import com.sanbafule.sharelock.comm.SString;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述：  解散群
 */
public class GroupKickConversationRow extends BaseConversationRow {
    public GroupKickConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position, RoundNumberDragListener dragListener) {

        if (holder instanceof GroupDismissHolder) {
            MessageContent latestMessage = conversation.getLatestMessage();
            if (latestMessage instanceof TextMessage) {
                ((BaseConversationHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
                ((BaseConversationHolder) holder).nameTextView.setText(context.getString(R.string.group_about_info_notice));
                ((BaseConversationHolder) holder).setReceiveDate(conversation);

                ((BaseConversationHolder) holder).setBase(conversation, context, holder.getLayoutPosition(), conversation.getSenderUserId(), dragListener);

                ((BaseConversationHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseConversationHolder) holder).clearUnReadMessage(context, holder.getLayoutPosition(), ((GroupDismissHolder) holder).readTextView, conversation.getSenderUserId());

                        // 跳转群组列表页面
                        context.startActivity(new Intent(context, GroupNotificationActivity.class)
                                .putExtra(SString.TARGETNAME, conversation.getSenderUserId()));
                    }
                });
//                {"groupId":"27","operation":"INVITATION_USER_ADD_GROUP","groupName":"查房","nowStatus":0,"requestId":"24","targetUserId":"xcy001","sourceUserId":"zhouliwen22"}
                try {
                    JSONObject object = new JSONObject(((TextMessage) latestMessage).getContent());
                    String groupId = object.getString("groupId");
                    //解散群组操作
                    ((GroupDismissHolder) holder).setGroupKickBody(groupId, context.getString(R.string.group_member_kick));

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
        return ConversationRowType.GROUP_DISMISS.getId();
    }
}
