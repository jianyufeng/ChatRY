/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sanbafule.sharelock.chatting.modle.message;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.VoiceRowViewHolder;

import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

/**
 *
 *
 */
public class VoiceTxRow extends BaseChattingRow {

    public VoiceTxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_to_voice);

            //use the view holder pattern to save of already looked up subviews
            VoiceRowViewHolder holder = new VoiceRowViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(final Context context, BaseHolder baseHolder,
                                  final ShareLockMessage shareLockMessage, int position) {
        if (shareLockMessage == null || shareLockMessage.getMessage() == null) {

            return;
        }
        final Message detail = shareLockMessage.getMessage();

        final VoiceRowViewHolder holder = (VoiceRowViewHolder) baseHolder;
        holder.voiceAnim.setVoiceFrom(false);
        holder.initVoiceRow(holder, shareLockMessage, position, (GroupChattingActivity) context, false);
        View.OnClickListener onClickListener = ((GroupChattingActivity) context).getChattingAdapter().getOnClickListener();
        getMsgStateResId(context, holder, shareLockMessage);
//        getMsgStateResId(position, holder, detail, onClickListener);
//        holder.getUploadState().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SChattingHelp.reSendMessage(shareLockMessage,((GroupChattingActivity) context).getChattingAdapter());
//            }
//        });

    }


    @Override
    public int getChatViewType() {
        return ChattingRowType.VOICE_ROW_TRANSMIT.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, Message detail) {
        // TODO Auto-generated method stub
        return false;
    }

}
