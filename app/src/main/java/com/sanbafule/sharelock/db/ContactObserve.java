package com.sanbafule.sharelock.db;

import com.sanbafule.sharelock.chatting.db.OnMessageChange;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/3 15:03
 * cd : 三八妇乐
 * 描述：
 */
public class ContactObserve implements OnMessageChange {

    public static ContactObserve sInstance;

    public static synchronized ContactObserve getInstance() {
        if (sInstance == null) {
            sInstance = new ContactObserve();
        }
        return sInstance;
    }
    @Override
    public void onChanged(String sessionId) {

        ContactsCache.getInstance().reload();
    }
}
