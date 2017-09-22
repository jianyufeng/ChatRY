package com.sanbafule.sharelock.chatting.db;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/13.
 */
public abstract class SObservable<T> {

    protected final ArrayList<T> mObservers = new ArrayList<T>();

    /**
     * 注册观察者
     * @param observer
     */
    public void registerObserver(T observer) {
        if (observer == null) {
            throw new NullPointerException("The observer is null.");
        }
        synchronized(mObservers) {
            if (mObservers.contains(observer)) {
//                throw new IllegalStateException("ECObservable " + observer + " is already registered.");
                mObservers.remove(observer);
//         return;
            }
            mObservers.add(observer);
        }
    }

    /**
     * 移除观察
     * @param observer
     */
    public void unregisterObserver(T observer) {
        if (observer == null) {
            throw new NullPointerException("The observer is null.");
        }
        synchronized(mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
//                throw new IllegalStateException("ECObservable " + observer + " was not registered.");
                return;
            }
            mObservers.remove(index);
        }
    }

    /**
     * 移除所有观察着
     */
    public void unregisterAll() {
        synchronized(mObservers) {
            mObservers.clear();
        }
    }
}
