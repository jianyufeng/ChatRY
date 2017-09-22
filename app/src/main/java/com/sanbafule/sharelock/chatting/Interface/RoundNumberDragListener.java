package com.sanbafule.sharelock.chatting.Interface;

import com.sanbafule.sharelock.view.RoundNumber;

import io.rong.imlib.model.Conversation;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/16 18:00
 * cd : 三八妇乐
 * 描述：
 */
public interface RoundNumberDragListener {
    public void onDown(RoundNumber view);
    public void onMove(float curX, float curY);
    public void onUp(Conversation conversation,String name);
}
