package com.sanbafule.sharelock.chatting.Interface;

import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2016/10/20.
 */
public interface SendMessageCallBack {

    void onSuccess(ShareLockMessage message);
    void onError(ShareLockMessage message, RongIMClient.ErrorCode code);
    void onProgress(ShareLockMessage message, int arg0);
}
