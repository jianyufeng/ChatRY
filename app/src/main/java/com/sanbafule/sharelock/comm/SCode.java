package com.sanbafule.sharelock.comm;

import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.modle.ContactInfo;

/**
 * Created by Administrator on 2016/10/19.
 */
public final class SCode {

    public static final int PHOTO_REQUESTCODE=101;

    // 重新请求服务器
    public static final int  AGAIN_LOGIN_CODE=0x11;
    // 无需重新请求服务器
    public static final int  NOT_AGAIN_LOGIN_CODE=0x12;

    // 图片消息原图的最大宽度
    public static final int SC_MAX_WIDTH = 720;
    // 图片消息原图的最大高度
    public static final int SC_MAX_HEIGHT = 1280;

    public static final int THUMB_MAX_WIDTH = 80;

}
