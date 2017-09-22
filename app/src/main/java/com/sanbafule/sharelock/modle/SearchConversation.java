package com.sanbafule.sharelock.modle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/28 10:15
 * cd : 三八妇乐
 * 描述：
 */
public class SearchConversation implements Parcelable {
    public SearchConversation() {
    }
    public Conversation getCnversation() {
        return cnversation;
    }

    public void setCnversation(Conversation cnversation) {
        this.cnversation = cnversation;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<TextMessage> getList() {
        return list;
    }

    public void setList(ArrayList<TextMessage> list) {
        this.list = list;
    }

    // 会话
    private Conversation cnversation;
    // 消息数量
    private int count;

    private ArrayList<TextMessage> list;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.cnversation, flags);
        dest.writeInt(this.count);
        dest.writeTypedList(this.list);
    }

    protected SearchConversation(Parcel in) {
        this.cnversation = in.readParcelable(Conversation.class.getClassLoader());
        this.count = in.readInt();
        this.list = in.createTypedArrayList(TextMessage.CREATOR);
    }

    public static final Parcelable.Creator<SearchConversation> CREATOR = new Parcelable.Creator<SearchConversation>() {
        @Override
        public SearchConversation createFromParcel(Parcel source) {
            return new SearchConversation(source);
        }

        @Override
        public SearchConversation[] newArray(int size) {
            return new SearchConversation[size];
        }
    };
}
