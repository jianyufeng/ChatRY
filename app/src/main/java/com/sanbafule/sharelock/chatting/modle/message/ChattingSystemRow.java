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
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.SystemViewHolder;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.LogUtil;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RecallNotificationMessage;


/**
 * ShareLock
 * 好友验证
 */
public class ChattingSystemRow extends BaseChattingRow {

    public ChattingSystemRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        // we have a don't have a converView so we'll have to create a new one
        if (convertView == null || convertView.getTag() == null || ((BaseHolder) convertView.getTag()).getType() != mRowType) {
            convertView = inflater.inflate(R.layout.chatting_item_system, null);
            // use the view holder pattern to save of already looked up subviews
            SystemViewHolder holder = new SystemViewHolder(mRowType);
            holder.setChattingTime((TextView) convertView.findViewById(R.id.chatting_time_tv));
            holder.mSystemView = (TextView) convertView.findViewById(R.id.chatting_content_itv);
            convertView.setTag(holder);
        }
        return convertView;
    }

    @Override
    public void buildChattingData(Context context, BaseHolder baseHolder,
                                  ShareLockMessage detail, int position) {


        if (detail == null || detail.getMessage() == null) {
            return;
        }
        SystemViewHolder holder = (SystemViewHolder) baseHolder;
        Message message = detail.getMessage();
        if (message != null) {
            MessageContent content = message.getContent();

            if (content instanceof RecallNotificationMessage) {
                LogUtil.i(((RecallNotificationMessage) content).getOperatorId());
                LogUtil.i(((RecallNotificationMessage) content).getOriginalObjectName());
                LogUtil.i(((RecallNotificationMessage) content).getRecallTime() + "");
                holder.mSystemView.setText("撤回了一条消息");

            } else if (message.getConversationType() == Conversation.ConversationType.GROUP && content instanceof InformationNotificationMessage) {
                //群组中的小灰条
                InformationNotificationMessage infoNTF = (InformationNotificationMessage) content;
                String extra = infoNTF.getExtra();
                if (TextUtils.isEmpty(extra)) {
                    extra = "";
                } else if (extra.equals(ShareLockManager.getInstance().getUserName())) {
                    extra = "你";
                } else {
                    ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(extra);
                    if (info != null) {
                        extra = info.getName();
                    }
                }
                String msg = infoNTF.getMessage();

                if (!TextUtils.isEmpty(msg)) {
                    holder.mSystemView.setText(extra + " " + msg);
                }
            } else if (message.getConversationType() == Conversation.ConversationType.PRIVATE &&
                    content instanceof CommandNotificationMessage) {
                ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(message.getSenderUserId());
                String name = null;
                if (info != null) {
                    name = info.getName();
                }
                holder.mSystemView.setText(context.getString(R.string.friend_del_msg, name));

            }

        }

    }


    // 处理点击事件
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }


    @Override
    public int getChatViewType() {

        return ChattingRowType.CHATTING_SYSTEM_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, Message detail) {

        return false;
    }


}
