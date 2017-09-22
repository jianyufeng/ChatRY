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
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.DescriptionViewHolder;

import io.rong.imlib.model.Message;


/**
 * 聊天页面的对方的文本消息条
 *
 */
public class CallRxRow extends BaseChattingRow {

	
	public CallRxRow(int type){
		super(type);
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, Message detail) {
		return false;
	}

	@Override
	protected void buildChattingData(Context context, BaseHolder baseHolder, ShareLockMessage detail, int position) {

		DescriptionViewHolder holder = (DescriptionViewHolder) baseHolder;
//		Message message = detail;
//		if(message != null&&message.getType()== Message.Type.CALL) {
//			ECCallMessageBody textBody = (ECCallMessageBody) message.getBody();
//			holder.getDescTextView().setText(textBody.getCallText());
//			holder.getDescTextView().setMovementMethod(LinkMovementMethod.getInstance());
//		}
		
	}

	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null ) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_from);

            
            //use the view holder pattern to save of already looked up subviews
            DescriptionViewHolder holder = new DescriptionViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        } 
		return convertView;
	}
	
//	@Override
//	public int getChatViewType() {
//
//		return ChattingRowType.CALL_ROW_RECEIVED.ordinal();
//	}

}
