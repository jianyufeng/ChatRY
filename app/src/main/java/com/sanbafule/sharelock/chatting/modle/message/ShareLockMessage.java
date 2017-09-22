package com.sanbafule.sharelock.chatting.modle.message;

import android.os.Parcel;
import android.os.Parcelable;

import io.rong.imlib.model.Message;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/6 09:44
 * cd : 三八妇乐
 * 描述：
 */
public class ShareLockMessage implements Parcelable {

    private Message message;

    private int progress;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.message, flags);
        dest.writeInt(this.progress);
    }

    public ShareLockMessage() {
    }

    protected ShareLockMessage(Parcel in) {
        this.message = in.readParcelable(Message.class.getClassLoader());
        this.progress = in.readInt();
    }

    public static final Parcelable.Creator<ShareLockMessage> CREATOR = new Parcelable.Creator<ShareLockMessage>() {
        @Override
        public ShareLockMessage createFromParcel(Parcel source) {
            return new ShareLockMessage(source);
        }

        @Override
        public ShareLockMessage[] newArray(int size) {
            return new ShareLockMessage[size];
        }
    };
}
