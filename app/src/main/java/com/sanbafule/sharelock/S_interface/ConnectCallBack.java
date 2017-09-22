package com.sanbafule.sharelock.S_interface;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/22 10:46
 * cd : 三八妇乐
 * 描述：
 */
public interface ConnectCallBack {

    void onTokenIncorrect();

    void onSuccess(String s);

    void onError();

}
