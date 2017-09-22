package com.sanbafule.sharelock.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者:Created by 简玉锋 on 2016/11/28 15:46
 * 邮箱: jianyufeng@38.hn
 */

public class GroupMember implements Parcelable {

    /**
     * gid : 3
     * u_id : 114506
     * u_username : zhouliwen22
     * u_header :
     * gur_identity : 2
     * gur_card :
     */

    private String gid;
    private String u_id;
    private String u_username;
    private String u_header;
    private int gur_identity =-1;
    private String gur_card;

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public void setU_username(String u_username) {
        this.u_username = u_username;
    }

    public void setU_header(String u_header) {
        this.u_header = u_header;
    }

    public void setGur_identity(int gur_identity) {
        this.gur_identity = gur_identity;
    }

    public void setGur_card(String gur_card) {
        this.gur_card = gur_card;
    }

    public String getGid() {
        return gid;
    }

    public String getU_id() {
        return u_id;
    }

    public String getU_username() {
        return u_username;
    }

    public String getU_header() {
        return u_header;
    }

    public int getGur_identity() {
        return gur_identity;
    }

    public String getGur_card() {
        return gur_card;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gid);
        dest.writeString(this.u_id);
        dest.writeString(this.u_username);
        dest.writeString(this.u_header);
        dest.writeInt(this.gur_identity);
        dest.writeString(this.gur_card);
    }

    public GroupMember() {
    }

    protected GroupMember(Parcel in) {
        this.gid = in.readString();
        this.u_id = in.readString();
        this.u_username = in.readString();
        this.u_header = in.readString();
        this.gur_identity = in.readInt();
        this.gur_card = in.readString();
    }

    public static final Parcelable.Creator<GroupMember> CREATOR = new Parcelable.Creator<GroupMember>() {
        public GroupMember createFromParcel(Parcel source) {
            return new GroupMember(source);
        }

        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };
}
