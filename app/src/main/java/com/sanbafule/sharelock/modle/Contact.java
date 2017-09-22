package com.sanbafule.sharelock.modle;


import android.os.Parcel;
import android.os.Parcelable;

import com.sanbafule.sharelock.global.MyString;

/**
 * 联系人的Modle
 */
public class Contact implements Parcelable {

    // 用户名
    private String u_username;
    // 用户备注
    private String u_note;
    // 消息免打扰
    private boolean no_disturb;
    //是否是黑名单
    private boolean isBlack;
    //昵称
    private String u_nickname;

    private int type;

    public String getU_username() {
        return u_username;
    }

    public void setU_username(String u_username) {
        this.u_username = u_username;
    }

    public String getU_note() {
        return u_note;
    }

    public void setU_note(String u_note) {
        this.u_note = u_note;
    }

    public boolean isNo_disturb() {
        return no_disturb;
    }

    public void setNo_disturb(boolean no_disturb) {
        this.no_disturb = no_disturb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public String getU_nickname() {
        return u_nickname;
    }

    public String getName() {
        if (MyString.hasData(getU_note())) {
            return getU_note();
        } else if (MyString.hasData(getU_nickname())) {
            return getU_nickname();
        } else {
            return getU_username();
        }

    }

    public void setU_nickname(String u_nickname) {
        this.u_nickname = u_nickname;
    }

    public Contact() {
    }

    public Contact(String userName, int type) {
        this.u_username = userName;
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.u_username);
        dest.writeString(this.u_note);
        dest.writeByte(this.no_disturb ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeByte(this.isBlack ? (byte) 1 : (byte) 0);
        dest.writeString(this.u_nickname);
    }

    protected Contact(Parcel in) {
        this.u_username = in.readString();
        this.u_note = in.readString();
        this.no_disturb = in.readByte() != 0;
        this.type = in.readInt();
        this.isBlack = in.readByte() != 0;
        this.u_nickname = in.readString();

    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
