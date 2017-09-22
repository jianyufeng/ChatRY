package com.sanbafule.sharelock.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/16.
 */
public class NewFriend implements Parcelable {
    public NewFriend(){}
    private int frl_id;
    private String frl_claimantUserName;
    private String frl_friendsUserName;
    private String frl_requestNote;
    private int frl_status;

    public int getFrl_id() {
        return frl_id;
    }

    public void setFrl_id(int frl_id) {
        this.frl_id = frl_id;
    }

    public String getFrl_claimantUserName() {
        return frl_claimantUserName;
    }

    public void setFrl_claimantUserName(String frl_claimantUserName) {
        this.frl_claimantUserName = frl_claimantUserName;
    }

    public String getFrl_friendsUserName() {
        return frl_friendsUserName;
    }

    public void setFrl_friendsUserName(String frl_friendsUserName) {
        this.frl_friendsUserName = frl_friendsUserName;
    }

    public String getFrl_requestNote() {
        return frl_requestNote;
    }

    public void setFrl_requestNote(String frl_requestNote) {
        this.frl_requestNote = frl_requestNote;
    }

    public int getFrl_status() {
        return frl_status;
    }

    public void setFrl_status(int frl_status) {
        this.frl_status = frl_status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.frl_id);
        dest.writeString(this.frl_claimantUserName);
        dest.writeString(this.frl_friendsUserName);
        dest.writeString(this.frl_requestNote);
        dest.writeInt(this.frl_status);
    }

    protected NewFriend(Parcel in) {
        this.frl_id = in.readInt();
        this.frl_claimantUserName = in.readString();
        this.frl_friendsUserName = in.readString();
        this.frl_requestNote = in.readString();
        this.frl_status = in.readInt();
    }

    public static final Creator<NewFriend> CREATOR = new Creator<NewFriend>() {
        @Override
        public NewFriend createFromParcel(Parcel source) {
            return new NewFriend(source);
        }

        @Override
        public NewFriend[] newArray(int size) {
            return new NewFriend[size];
        }
    };
}
