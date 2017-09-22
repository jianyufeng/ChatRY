/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sanbafule.sharelock.chatting.modle.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanbafule.sharelock.R;

import io.rong.imlib.model.Message;


/**
 * @author 容联•云通讯
 * @version 4.0
 * @date 2014-12-9
 */
public class BaseHolder {

    /**
     * row type {@link com.sanbafule.sharelock.chatting.help.ChattingRowType}
     */
    protected int type;
    //头像
    protected ImageView chattingAvatar;
    // 时间
    protected TextView chattingTime;
    //别人
    protected TextView chattingUser;
    // 选中的
    protected CheckBox checkBox;

    /**
     * The file Im message transmission state, success or failure or sending
     *
     * @see io.rong.imlib.model.Message#
     * @see io.rong.imlib.model.Message.SentStatus SENDING(10),
     * @see io.rong.imlib.model.Message.SentStatus FAILED(20),
     * @see io.rong.imlib.model.Message.SentStatus SENT(30),
     * @see io.rong.imlib.model.Message.SentStatus RECEIVED(40),
     * @see io.rong.imlib.model.Message.SentStatus READ(50),
     * @see io.rong.imlib.model.Message.SentStatus DESTROYED(60);
     */
    //消息发送失败
    protected ImageView uploadState;
    //消息正在发送中
    protected ProgressBar progressBar;
    //
    protected View baseView;
    protected View clickAreaView;
    protected View chattingMaskView;

    public BaseHolder(int type) {
        this.type = type;
    }

    /**
     * @param baseView
     */
    public BaseHolder(View baseView) {
        super();
        this.baseView = baseView;
    }

    public void initBaseHolder(View baseView) {
        this.baseView = baseView;
        chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
        chattingAvatar = (ImageView) baseView.findViewById(R.id.chatting_avatar_iv);
        chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
//		clickAreaView = baseView.findViewById(R.id.chatting_click_area);
        uploadState = (ImageView) baseView.findViewById(R.id.chatting_state_iv);
        progressBar = (ProgressBar) baseView.findViewById(R.id.uploading_pb);

    }


    /**
     * @param edit
     */
    public void setEditMode(boolean edit) {
        int visibility = edit ? View.VISIBLE : View.GONE;
        if (checkBox != null && checkBox.getVisibility() != visibility) {
            checkBox.setVisibility(visibility);
        }

        if (chattingMaskView != null && chattingMaskView.getVisibility() != visibility) {
            chattingMaskView.setVisibility(visibility);
        }

    }


    /**
     * @return the baseView
     */
    public View getBaseView() {
        return baseView;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the progressBar
     */
    public ProgressBar getUploadProgressBar() {
        return progressBar;
    }

    /**
     * @return the chattingAvatar
     */
    public ImageView getChattingAvatar() {
        return chattingAvatar;
    }

    /**
     * @return the chattingTime
     */
    public TextView getChattingTime() {
        return chattingTime;
    }

    /**
     * @param chattingTime the chattingTime to set
     */
    public void setChattingTime(TextView chattingTime) {
        this.chattingTime = chattingTime;
    }

    /**
     * @return the chattingUser
     */
    public TextView getChattingUser() {
        return chattingUser;
    }

    /**
     * @return the checkBox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * @return the uploadState
     */
    public ImageView getUploadState() {
        return uploadState;
    }

    /**
     * @return the clickAreaView
     */
    public View getClickAreaView() {
        return clickAreaView;
    }

    /**
     * @return the chattingMaskView
     */
    public View getChattingMaskView() {
        return chattingMaskView;
    }


}
