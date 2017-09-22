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
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.VoiceRowViewHolder;

import io.rong.imlib.model.Message;


public class VoiceRxRow extends BaseChattingRow {

    public VoiceRxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_from_voice);

            VoiceRowViewHolder holder = new VoiceRowViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(Context context, BaseHolder baseHolder,
                                  final ShareLockMessage detail, int position) {

        if (detail == null || detail.getMessage() == null) {
            return;
        }
        VoiceRowViewHolder holder = (VoiceRowViewHolder) baseHolder;
//        Message message = detail.getMessage();
        holder.initVoiceRow(holder, detail, position, (GroupChattingActivity) context, true);
        holder.voiceAnim.setVoiceFrom(true);
    }


    @Override
    public int getChatViewType() {

        return ChattingRowType.VOICE_ROW_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, Message detail) {
        // TODO Auto-generated method stub
        return false;
    }

}