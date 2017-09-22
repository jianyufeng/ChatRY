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
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.ViewHolderTag;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.DescriptionViewHolder;
import com.sanbafule.sharelock.util.SimpleCommonUtils;

import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;


public class DescriptionRxRow extends BaseChattingRow {


    public DescriptionRxRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, Message detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, final ShareLockMessage detail, int position) {
        if (detail == null || detail.getMessage() == null) {
            return;
        }
        DescriptionViewHolder holder = (DescriptionViewHolder) baseHolder;
        Message message = detail.getMessage();
        if (message.getContent() instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message.getContent();
            String content = textMessage.getContent();
            SimpleCommonUtils.spannableEmoticonFilter(holder.getDescTextView(), content);
            holder.getDescTextView().setMovementMethod(LinkMovementMethod.getInstance());
            holder.getDescTextView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ((GroupChattingActivity)context).handLeftText(detail);
//                    handLeftText(context, detail);
                    return true;
                }
            });
        }

    }

    private void handLeftText(final Context context, final ShareLockMessage shareLockMessage) {

    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_from);
            DescriptionViewHolder holder = new DescriptionViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }


    @Override
    public int getChatViewType() {

        return ChattingRowType.DESCRIPTION_ROW_RECEIVED.ordinal();
    }


}