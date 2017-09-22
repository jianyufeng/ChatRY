package com.sanbafule.sharelock.modle;

import android.os.Parcel;
import android.os.Parcelable;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/27.
 */
public class ClientUser implements Parcelable {


    private int u_id;
    //性别
    private int u_sex;
    // userName
    private String u_username;
    // E-mail
    private String u_email;
    // 用户头像
    private String u_header;
    // 二维码
    private String u_qr_code;
    // 本地服务器的token
    private String token;
    // 融云的Token
    private String rongLibToken;
    // 用户登录版本号
    private int loginVersion;
    // 昵称
    private String u_nickname;
    //用户类型
    private String user_type;
    //用户级别名称
    private String user_level;
    // 个性签名
    public String getU_signature() {
        return u_signature;
    }

    public void setU_signature(String u_signature) {
        this.u_signature = u_signature;
    }

    //用户的个性签名
    private String u_signature;

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public int getU_sex() {
        return u_sex;
    }

    public void setU_sex(int u_sex) {
        this.u_sex = u_sex;
    }

    public String getU_username() {
        return u_username;
    }

    public void setU_username(String u_username) {
        this.u_username = u_username;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRongLibToken() {
        return rongLibToken;
    }

    public void setRongLibToken(String rongLibToken) {
        this.rongLibToken = rongLibToken;
    }

    public int getLoginVersion() {
        return loginVersion;
    }

    public void setLoginVersion(int loginVersion) {
        this.loginVersion = loginVersion;
    }

    public String getU_nickname() {
        return u_nickname;
    }

    public void setU_nickname(String u_nickname) {
        this.u_nickname = u_nickname;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.u_id);
        dest.writeInt(this.u_sex);
        dest.writeString(this.u_username);
        dest.writeString(this.u_email);
        dest.writeString(this.u_header);
        dest.writeString(this.u_qr_code);
        dest.writeString(this.token);
        dest.writeString(this.rongLibToken);
        dest.writeInt(this.loginVersion);
        dest.writeString(this.u_nickname);
        dest.writeString(this.user_type);
        dest.writeString(this.user_level);
        dest.writeString(this.u_signature);
    }

    public ClientUser() {
    }

    protected ClientUser(Parcel in) {
        this.u_id = in.readInt();
        this.u_sex = in.readInt();
        this.u_username = in.readString();
        this.u_email = in.readString();
        this.u_header = in.readString();
        this.u_qr_code = in.readString();
        this.token = in.readString();
        this.rongLibToken = in.readString();
        this.loginVersion = in.readInt();
        this.u_nickname = in.readString();
        this.user_level = in.readString();
        this.user_type = in.readString();
        this.u_signature = in.readString();
    }

    public static final Creator<ClientUser> CREATOR = new Creator<ClientUser>() {
        @Override
        public ClientUser createFromParcel(Parcel source) {
            return new ClientUser(source);
        }

        @Override
        public ClientUser[] newArray(int size) {
            return new ClientUser[size];
        }
    };


    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("u_id", u_id);
            jsonObject.put("u_sex", u_sex);
            jsonObject.put("u_username", u_username);
            jsonObject.put("u_email", u_email);
            jsonObject.put("u_header", u_header);
            jsonObject.put("u_qr_code", u_qr_code);
            jsonObject.put("token", token);
            jsonObject.put("rongLibToken", rongLibToken);
            jsonObject.put("loginVersion", loginVersion);
            jsonObject.put("u_nickname", u_nickname);
            jsonObject.put("user_level", user_level);
            jsonObject.put("user_type", user_type);
            jsonObject.put("u_signature", u_signature);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "ClientUser{" +
                "u_id='" + u_id + '\'' +
                ", u_sex='" + u_sex + '\'' +
                ", u_username='" + u_username + '\'' +
                ", u_email='" + u_email + '\'' +
                ", u_header='" + u_header + '\'' +
                ", u_qr_code='" + u_qr_code + '\'' +
                ", token='" + token + '\'' +
                ", rongLibToken='" + rongLibToken + '\'' +
                ", loginVersion='" + loginVersion + '\'' +
                ", u_nickname='" + u_nickname + '\'' +
                ", user_type='" + user_type + '\'' +
                ", user_level='" + user_level + '\'' +
                ", u_signature='" + u_signature + '\'' +
                '}';


    }

    public ClientUser from(String input) {
        JSONObject object = null;
        try {
            object = new JSONObject(input);
            if (object.has("u_id")) {
                this.u_id = object.getInt("u_id");
            }
            if (object.has("u_sex")) {
                this.u_sex = object.getInt("u_sex");
            }
            if (object.has("u_username")) {
                this.u_username = object.getString("u_username");
            }
            if (object.has("u_email")) {
                this.u_email = object.getString("u_email");
            }
            if (object.has("u_header")) {
                this.u_header = object.getString("u_header");
            }
            if (object.has("u_qr_code")) {
                this.u_qr_code = object.getString("u_qr_code");
            }
            if (object.has("token")) {
                this.token = object.getString("token");
            }
            if (object.has("rongLibToken")) {
                this.rongLibToken = object.getString("rongLibToken");
            }
            if (object.has("loginVersion")) {
                this.loginVersion = object.getInt("loginVersion");
            }

            if (object.has("u_nickname")) {
                this.u_nickname = object.getString("u_nickname");
            }
            if (object.has("user_type")) {
                this.u_nickname = object.getString("user_type");
            }
            if (object.has("user_level")) {
                this.u_nickname = object.getString("user_level");
            }
            if (object.has("u_signature")) {
                this.u_nickname = object.getString("u_signature");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

}
