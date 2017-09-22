package com.sanbafule.sharelock.chatting.modle;

import android.os.Parcel;
import android.os.Parcelable;


public class ViewImageInfo implements Parcelable {

    //
    private int index;
    //本地id
    private String msgLocalId;
    //    缩略图路径
    private String thumbnailurl;
    // 大图路径
    private String picurl;
    //是否已经下载
    private boolean isDownload;
    // 是否是gif
    private boolean isGif;
    // 图片的发送者或者接收者
    private String userName;

    public ViewImageInfo() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMsgLocalId() {
        return msgLocalId;
    }

    public void setMsgLocalId(String msgLocalId) {
        this.msgLocalId = msgLocalId;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private ViewImageInfo(Parcel in) {
        this.index = in.readInt();
        this.msgLocalId = in.readString();
        this.thumbnailurl = in.readString();
        this.picurl = in.readString();
        this.isDownload = (in.readByte() != 0);
        this.isGif = (in.readByte() != 0);
        this.userName = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.msgLocalId);
        dest.writeString(this.thumbnailurl);
        dest.writeString(this.picurl);
        dest.writeByte((byte) (this.isDownload ? 1 : 0));
        dest.writeByte((byte) (this.isGif ? 1 : 0));
        dest.writeString(this.userName);
    }

    public static final Creator<ViewImageInfo> CREATOR
            = new Creator<ViewImageInfo>() {
        public ViewImageInfo createFromParcel(Parcel in) {
            return new ViewImageInfo(in);
        }

        public ViewImageInfo[] newArray(int size) {
            return new ViewImageInfo[size];
        }
    };

}
