package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;

import io.rong.imlib.model.Conversation;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:58
 * cd : 三八妇乐
 * 描述：
 */
public  abstract class BaseConversationRow implements SConversationRow {
    public int mRowType;

    public BaseConversationRow(int mRowType) {
        this.mRowType = mRowType;
    }

    @Override
    public void buildConversationBaseData(RecyclerView.ViewHolder holder, Context context, Conversation conversation, int position,RoundNumberDragListener dragListener) {
        buildConversationData(holder,context,conversation,position, dragListener);
    }

    @Override
    public int getConversationViewType() {
        return 0;
    }

    public  abstract  void buildConversationData(RecyclerView.ViewHolder holder, Context context, Conversation conversation, int position,RoundNumberDragListener dragListener) ;
}
