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

import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.adapter.ChattingAdapter;
import com.sanbafule.sharelock.chatting.modle.ViewHolderTag;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.chatting.view.CCPAnimImageView;
import com.sanbafule.sharelock.util.DensityUtil;

import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;


/**
 * @author 容联•云通讯
 * @version 4.0
 * @date 2014-12-9
 */
public class VoiceRowViewHolder extends BaseHolder {

    public TextView contentTv;
    public TextView voicePlayAnim;
    public CCPAnimImageView voiceAnim;
    public FrameLayout voicePlayFrameLayout;
    public CCPAnimImageView voiceLoading;
    public TextView voiceSendigBG;

    /**
     * @param type
     */
    public VoiceRowViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);

        chattingTime = ((TextView) baseView.findViewById(R.id.chatting_time_tv));
        chattingUser = ((TextView) baseView.findViewById(R.id.chatting_user_tv));
        voicePlayAnim = ((TextView) baseView.findViewById(R.id.chatting_voice_play_anim_tv));
        checkBox = ((CheckBox) baseView.findViewById(R.id.chatting_checkbox));
        chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
        uploadState = ((ImageView) baseView.findViewById(R.id.chatting_state_iv));
        contentTv = ((TextView) baseView.findViewById(R.id.chatting_content_itv));
        voicePlayFrameLayout = ((FrameLayout) baseView.findViewById(R.id.chatting_voice_play_content));
        voiceAnim = ((CCPAnimImageView) baseView.findViewById(R.id.chatting_voice_anim));
        voiceAnim.restBackground();

//        if (receive) {
//            type = 5;
//            voiceAnim.setVoiceFrom(true);
//            voiceLoading = ((CCPAnimImageView) baseView.findViewById(R.id.chatting_voice_loading));
//            voiceLoading.setVoiceFrom(true);
//            voiceLoading.restBackground();
//            return this;
//        }


//        voiceSendigBG = ((TextView) baseView.findViewById(R.id.chatting_voice_sending_bg));
        voiceAnim.setVoiceFrom(false);
        type = 6;
        return this;


    }


    public void initVoiceRow(VoiceRowViewHolder holder, final ShareLockMessage shareLockMessage, int position, final GroupChattingActivity activity, final boolean receive) {
        if (holder == null) {
            return;
        }
        VoiceMessage message;
        Message detail=shareLockMessage.getMessage();
        if (detail.getContent() instanceof VoiceMessage) {
            message = (VoiceMessage) detail.getContent();
        } else {
            return;
        }

        int duration = message.getDuration();
        if (duration < 1) {
            duration = 1;
        }
        holder.voiceAnim.setVisibility(View.GONE);
        ViewHolderTag holderTag = ViewHolderTag.createTag(detail, ViewHolderTag.TagType.TAG_VOICE, position, holder.type, receive);
        holder.voicePlayAnim.setTag(holderTag);
        holder.voicePlayAnim.setOnClickListener(activity.getChattingAdapter().getOnClickListener());
//        View.OnLongClickListener onLongClickListener =
//                activity.getChattingAdapter().getOnLongClickListenner();
        holder.voicePlayAnim.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!receive){
                    activity.handLeftVoice(shareLockMessage);
                }else {
                    activity.handRightVoice(shareLockMessage);
                }
                return true;
            }
        });
        ChattingAdapter adapterForce = activity.getChattingAdapter();
        if (adapterForce.mVoicePosition == position) {
//            uploadVoiceStatus(holder, View.GONE, View.VISIBLE, receive);
            holder.voiceAnim.setVisibility(View.VISIBLE);
            holder.voiceAnim.startVoiceAnimation();
            holder.voiceAnim.setWidth(DensityUtil.fromDPToPix(activity, getTimeWidth(duration)));
            holder.contentTv.setTextColor(Color.parseColor("#7390A0"));
            holder.contentTv.setShadowLayer(2.0F, 1.2F, 1.2F, Color.parseColor("#ffffffff"));
            holder.contentTv.setVisibility(View.VISIBLE);
            holder.contentTv.setText(activity.getString(R.string.fmt_time_length, duration));
            holder.voicePlayAnim.setWidth(DensityUtil.fromDPToPix(activity, getTimeWidth(duration)));
            return;
        } else {
            holder.voiceAnim.stopVoiceAnimation();
            holder.voiceAnim.setVisibility(View.GONE);
        }
        holder.contentTv.setTextColor(Color.parseColor("#7390A0"));
        holder.contentTv.setShadowLayer(2.0F, 1.2F, 1.2F, Color.parseColor("#ffffffff"));
        holder.contentTv.setVisibility(View.VISIBLE);
        holder.contentTv.setText(activity.getString(R.string.fmt_time_length, duration));
        holder.voiceAnim.setWidth(DensityUtil.fromDPToPix(activity, getTimeWidth(duration)));
        holder.voicePlayAnim.setWidth(DensityUtil.fromDPToPix(activity, getTimeWidth(duration)));
        // 显示消息的发送状态

        if (!receive) {
            holder.voiceAnim.setBackgroundResource(R.drawable.chatto_bg);
            holder.voicePlayAnim.setBackgroundResource(R.drawable.chatto_bg);
        } else {
            holder.voiceAnim.setBackgroundResource(R.drawable.chatfrom_bg);
            holder.voicePlayAnim.setBackgroundResource(R.drawable.chatfrom_bg);
            //消息是一睹 0 未读  1 已读
            final Message.ReceivedStatus receivedStatus = detail.getReceivedStatus();
            if (receivedStatus.isRead()) {
                holder.uploadState.setVisibility(View.GONE);
            } else {
                holder.uploadState.setVisibility(View.VISIBLE);
            }

        }
        holder.contentTv.setBackgroundColor(0);
    }

    /**
     * @param time
     * @return
     */
    public static int getTimeWidth(int time) {
        if (time <= 2)
            return 80;
        if (time < 10)
            return (80 + 9 * (time - 2));
        if (time < 60)
            return (80 + 9 * (7 + time / 10));
        return 204;
    }
}
