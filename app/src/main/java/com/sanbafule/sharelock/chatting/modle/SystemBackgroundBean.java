package com.sanbafule.sharelock.chatting.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/3 10:39
 * cd : 三八妇乐
 * 描述：
 */
public class SystemBackgroundBean implements Parcelable {

    // 是否下载
    private boolean isDownloaded;
    // 图片地址
    private String url;
    // 是否选中
    private boolean selected;

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDownloaded ? (byte) 1 : (byte) 0);
        dest.writeString(this.url);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public SystemBackgroundBean() {
    }

    protected SystemBackgroundBean(Parcel in) {
        this.isDownloaded = in.readByte() != 0;
        this.url = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SystemBackgroundBean> CREATOR = new Parcelable.Creator<SystemBackgroundBean>() {
        @Override
        public SystemBackgroundBean createFromParcel(Parcel source) {
            return new SystemBackgroundBean(source);
        }

        @Override
        public SystemBackgroundBean[] newArray(int size) {
            return new SystemBackgroundBean[size];
        }
    };
}
