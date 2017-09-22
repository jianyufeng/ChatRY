package com.sanbafule.sharelock.modle;

/**
 * 作者:Created by 简玉锋 on 2016/12/23 14:27
 * 邮箱: jianyufeng@38.hn
 */

public class BanMember {
    /**
     * time : String
     * userId : String
     */

    private String time;
    private String userId;

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }
}
