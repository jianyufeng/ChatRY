package com.sanbafule.sharelock.modle;


import android.os.Parcel;
import android.os.Parcelable;

import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.global.MyString;

public class ContactInfo implements Parcelable {

    private int u_id;
    private String u_username;
    private String u_nickname;
    private String u_email;
    private int u_sex;
    private String u_header;
    private String u_qr_code;
    private String u_signature;
    private int u_create_time;
    private String user_type;
    private String user_level;
    private String comment_name;


    public String getU_signature() {
        return u_signature;
    }

    public void setU_signature(String u_signature) {
        this.u_signature = u_signature;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getU_username() {
        return u_username;
    }

    public void setU_username(String u_username) {
        this.u_username = u_username;
    }

    public String getU_nickname() {
        return u_nickname;
    }

    public void setU_nickname(String u_nickname) {
        this.u_nickname = u_nickname;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public int getU_sex() {
        return u_sex;
    }

    public void setU_sex(int u_sex) {
        this.u_sex = u_sex;
    }

    public String getU_header() {
        return u_header;
    }

    public void setU_header(String u_header) {
        this.u_header = u_header;
    }

    public String getU_qr_code() {
        return u_qr_code;
    }

    public void setU_qr_code(String u_qr_code) {
        this.u_qr_code = u_qr_code;
    }

    public int getU_create_time() {
        return u_create_time;
    }

    public void setU_create_time(int u_create_time) {
        this.u_create_time = u_create_time;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }

    public void setComment_name(String comment_name) {
        //更新数据库
        ContactSqlManager.updateNote(getU_username(),comment_name);
        this.comment_name = comment_name;
    }

    public String getComment_name() {
        if(comment_name!=null){
            return comment_name;
        }else {
            return ContactSqlManager.getContactNote(getU_username());
        }

        // 查询数据库
    }

    public String getName() {
        if(MyString.hasData(getComment_name())){
            return getComment_name();
        }else if (MyString.hasData(getU_nickname())) {
            return getU_nickname();
        } else if (MyString.hasData(getU_username())) {
            return getU_username();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.u_id);
        dest.writeString(this.u_username);
        dest.writeString(this.u_nickname);
        dest.writeString(this.u_email);
        dest.writeInt(this.u_sex);
        dest.writeString(this.u_header);
        dest.writeString(this.u_qr_code);
        dest.writeInt(this.u_create_time);
        dest.writeString(this.user_type);
        dest.writeString(this.user_level);
        dest.writeString(this.u_signature);
        dest.writeString(this.comment_name);
    }

    public ContactInfo() {
    }

    protected ContactInfo(Parcel in) {
        this.u_id = in.readInt();
        this.u_username = in.readString();
        this.u_nickname = in.readString();
        this.u_email = in.readString();
        this.u_sex = in.readInt();
        this.u_header = in.readString();
        this.u_qr_code = in.readString();
        this.u_create_time = in.readInt();
        this.user_type = in.readString();
        this.user_level = in.readString();
        this.u_signature = in.readString();
        this.comment_name = in.readString();
    }


    public static final Parcelable.Creator<ContactInfo> CREATOR = new Parcelable.Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel source) {
            return new ContactInfo(source);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };


}
