package com.sanbafule.sharelock.chatting.modle;

/**
 * 作者:Created by 简玉锋 on 2017/2/8 17:10
 * 邮箱: jianyufeng@38.hn
 */

public class InsertSomeone {

    private String someBody;
    private String lastContent;
    private int insetPosition = 0;

    public String getAtSomebody() {
        return someBody;
    }

    public void setAtSomebody(String somebody) {
        this.someBody = somebody;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public String getLastContent() {
        return lastContent;
    }

    public int getInsertPos() {
        return insetPosition;
    }

    public void setInsertPos(int position) {
        this.insetPosition = position;
    }
}
