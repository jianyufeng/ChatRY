package com.sanbafule.sharelock.chatting.Interface;

import java.util.List;

import io.rong.imlib.model.Message;

/**
 *ShareLock
 *
 */
public interface GetLatestMessagesCallBack {

    void getMessagesCallBack(List<Message> list);
}
