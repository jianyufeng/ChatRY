package com.sanbafule.sharelock.chatting.modle.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;

import io.rong.imlib.model.Conversation;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/19 15:37
 * cd : 三八妇乐
 * 描述： 所有抽象的会话
 */
public interface SConversationRow {

    View onCreateConversation(LayoutInflater layoutInflater, ViewGroup viewGroup);


    void buildConversationBaseData(RecyclerView.ViewHolder holder, Context context, Conversation conversation, int position,RoundNumberDragListener dragListener);

    int getConversationViewType();
}
