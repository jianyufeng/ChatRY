package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.FriendDeleteHolder;
import com.sanbafule.sharelock.db.ContactSqlManager;

import io.rong.imlib.model.Conversation;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 删除好友
 */
public class FriendDeleteConversationRow   extends  BaseConversationRow{
    public FriendDeleteConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {
        if (holder instanceof FriendDeleteHolder) {
            ((FriendDeleteHolder) holder).headImageView.setImageResource(R.drawable.ic_launcher);
            ((FriendDeleteHolder) holder).nameTextView.setText("好友删除");
            ((FriendDeleteHolder) holder).setReceiveDate(conversation);

            ((FriendDeleteHolder) holder).setBase(conversation,context,holder.getLayoutPosition(),conversation.getSenderUserId(),dragListener);

            ((FriendDeleteHolder) holder).conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转新朋友列表页面
                    ((FriendDeleteHolder) holder).clearUnReadMessage(context,holder.getLayoutPosition(),((FriendDeleteHolder) holder).readTextView,conversation.getSenderUserId());
                }
            });
            String name = conversation.getSenderUserId();

            ((FriendDeleteHolder) holder).setConversationBody(name,context.getString(R.string.friend_del_msg));
            ContactSqlManager.deleteContact(name);

        }

    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_friend_add_conversion,viewGroup,false);
    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.FRIEND_DELETE.getId();
    }
}
