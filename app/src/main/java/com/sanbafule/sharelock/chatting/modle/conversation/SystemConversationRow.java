package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;

import io.rong.imlib.model.Conversation;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:40
 * cd : 三八妇乐
 * 描述： 系统推送的会话
 */
public class SystemConversationRow extends BaseConversationRow {

    public SystemConversationRow(int mRowType) {
        super(mRowType);
    }

    @Override
    public void buildConversationData(final RecyclerView.ViewHolder holder, final Context context, final Conversation conversation, final int position,RoundNumberDragListener dragListener) {

    }

    @Override
    public View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.item_normal_conversion,viewGroup,false);
    }

    @Override
    public int getConversationViewType() {
        return ConversationRowType.SYSTEM.getId();
    }
}
