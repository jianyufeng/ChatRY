package com.sanbafule.sharelock.chatting.modle.conversation.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanbafule.sharelock.R;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/20 16:05
 * cd : 三八妇乐
 * 描述：
 */
public class GroupHolder extends BaseConversationHolder {

    public ImageView mConversationSentStatus;
//    public TextView mRead;
    public TextView mGroupMember;
    // @ 自己
    public TextView atMe;
    public GroupHolder(View itemView) {
        super(itemView);
        mConversationSentStatus= (ImageView) itemView.findViewById(R.id.conversion_SentStatus);
//        mRead= (TextView) itemView.findViewById(R.id.read);
        mGroupMember= (TextView) itemView.findViewById(R.id.conversation_group_member_name);
        atMe = (TextView) itemView.findViewById(R.id.conversation_group_at_me);
    }

//    @Override
//    public RecyclerView.ViewHolder getViewHolder(int type) {
//        if (type == 2) {
//            return this;
//        }
//        return null;
//    }
}
