package com.sanbafule.sharelock.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/3.
 */
public class GroupInfo implements Parcelable {

    /**
     * g_id : 3
     * g_name : 简玉锋
     * g_header : files/2016/12/09/original_1481251638-acioszCH.jpg
     * g_type : 0
     * g_permission : 0
     * g_declared : null
     * g_create_time : 1481251698
     * g_admin_username : zhouliwen22
     * g_is_delete : 0
     * g_qrcode : null
     */

    private String g_id;
    private String g_name;
    private String g_header;
    private int g_type;
    private int g_permission;
    private String g_declared;
    private String g_create_time;
    private String g_admin_username;
    private String g_is_delete;

    public String getG_qrcode() {
        return g_qrcode;
    }

    public void setG_qrcode(String g_qrcode) {
        this.g_qrcode = g_qrcode;
    }

    private String g_qrcode;
    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public void setG_header(String g_header) {
        this.g_header = g_header;
    }

    public void setG_type(int g_type) {
        this.g_type = g_type;
    }

    public void setG_permission(int g_permission) {
        this.g_permission = g_permission;
    }

    public void setG_declared(String g_declared) {
        this.g_declared = g_declared;
    }

    public void setG_create_time(String g_create_time) {
        this.g_create_time = g_create_time;
    }

    public void setG_admin_username(String g_admin_username) {
        this.g_admin_username = g_admin_username;
    }

    public void setG_is_delete(String g_is_delete) {
        this.g_is_delete = g_is_delete;
    }

    public String getG_id() {
        return g_id;
    }

    public String getG_name() {
        return g_name;
    }

    public String getG_header() {
        return g_header;
    }

    public int getG_type() {
        return g_type;
    }

    public int getG_permission() {
        return g_permission;
    }

    public String getG_declared() {
        return g_declared;
    }

    public String getG_create_time() {
        return g_create_time;
    }

    public String getG_admin_username() {
        return g_admin_username;
    }

    public String getG_is_delete() {
        return g_is_delete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.g_id);
        dest.writeString(this.g_name);
        dest.writeString(this.g_header);
        dest.writeInt(this.g_type);
        dest.writeInt(this.g_permission);
        dest.writeString(this.g_declared);
        dest.writeString(this.g_create_time);
        dest.writeString(this.g_admin_username);
        dest.writeString(this.g_is_delete);
        dest.writeString(this.g_qrcode);
    }

    public GroupInfo() {
    }

    protected GroupInfo(Parcel in) {
        this.g_id = in.readString();
        this.g_name = in.readString();
        this.g_header = in.readString();
        this.g_type = in.readInt();
        this.g_permission = in.readInt();
        this.g_declared = in.readString();
        this.g_create_time = in.readString();
        this.g_admin_username = in.readString();
        this.g_is_delete = in.readString();
        this.g_qrcode = in.readString();
    }

    public static final Parcelable.Creator<GroupInfo> CREATOR = new Parcelable.Creator<GroupInfo>() {
        public GroupInfo createFromParcel(Parcel source) {
            return new GroupInfo(source);
        }

        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };
}
