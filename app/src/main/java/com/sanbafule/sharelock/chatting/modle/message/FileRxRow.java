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

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.FileRowViewHolder;
import com.sanbafule.sharelock.chatting.modle.holder.GifRowHolder;

import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * ShareLock
 * 文件接收
 *
 */
public class FileRxRow extends BaseChattingRow {

	public FileRxRow(int type) {
		super(type);
	}

	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
		// we have a don't have a converView so we'll have to create a new one
		if (convertView == null || convertView.getTag() == null) {
			convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_file_from);

			// use the view holder pattern to save of already looked up subviews
			FileRowViewHolder holder = new FileRowViewHolder(mRowType);
			convertView.setTag(holder.initBaseHolder(convertView, true));
		}
		return convertView;
	}

	@Override
	public void buildChattingData(final Context context, BaseHolder baseHolder,
			ShareLockMessage detail, int position) {

		if (detail == null || detail.getMessage() == null) {
			return;
		}
		final GifRowHolder holder = (GifRowHolder) baseHolder;
		final FileMessage message = (FileMessage) detail.getMessage().getContent();

		Glide.with(context).load(message.getMediaUrl()).asGif().into(holder.gifImageView);
//		FileRowViewHolder holder = (FileRowViewHolder) baseHolder;
//		OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment
//				.getChattingAdapter().getOnClickListener();
//		View.OnLongClickListener onLongClickListener=((ChattingActivity) context).mChattingFragment
//				.getChattingAdapter().getOnLongClickListenner();
//		ViewHolderTag holderTag = ViewHolderTag.createTag(detail,
//				ViewHolderTag.TagType.TAG_VIEW_FILE, position);
//		if (detail != null) {
//			Message msg = detail;
//			ECFileMessageBody body = (ECFileMessageBody) msg.getBody();
//			holder.contentTv.setText(body.getFileName());
//			String fileName = "";
//			String userData = msg.getUserData();
//
//			if (TextUtils.isEmpty(userData)) {
//			} else {
//				fileName = userData.substring(userData.indexOf("fileName=")
//						+ "fileName=".length(), userData.length());
//
//			}
//			if ("mp4".equals(FileUtils.getFileExt(fileName))&&detail.getType()==Type.VIDEO) {
//
//				ECVideoMessageBody videoBody = (ECVideoMessageBody) msg
//						.getBody();
//				holder.contentTv.setVisibility(View.GONE);
//				holder.contentTv.setTag(null);
//				holder.contentTv.setOnClickListener(null);
//				holder.fl.setVisibility(View.VISIBLE);
//
//				holder.ivVideoMp4.setVisibility(View.VISIBLE);
//				holder.ivVideoMp4.setOnLongClickListener(onLongClickListener);
//				holder.ivVideoMp4.setTag(holderTag);
//				holder.buPlayVideo.setOnClickListener(onClickListener);
//				holder.buPlayVideo.setTag(holderTag);
//				holder.tvFile.setVisibility(View.VISIBLE);
//
//				String text = IMessageSqlManager.qureyVideoMsgLength(msg
//						.getMsgId());
//
//				if (!TextUtils.isEmpty(text)) {
//					holder.tvFile.setText(DemoUtils.bytes2kb(Long.parseLong(text)));
//				}
//				File file = new File(FileAccessor.getFilePathName(),
//						body.getFileName() + "_thum.png");
//
//				if (file.exists()) {
//					Bitmap thumbBitmap = DemoUtils.decodeBitmap(file.getAbsolutePath(),400,247);
//					if(thumbBitmap!=null){
//						holder.ivVideoMp4.setImageBitmap(thumbBitmap);
//					}
//				}else {
//
//					holder.ivVideoMp4.setImageResource(R.drawable.delete_message);
//				}
//
//			} else {
//				holder.contentTv.setVisibility(View.VISIBLE);
//				holder.ivVideoMp4.setVisibility(View.GONE);
//				holder.fl.setVisibility(View.GONE);
//				holder.buPlayVideo.setTag(null);
//				holder.buPlayVideo.setOnClickListener(null);
//				holder.contentTv.setTag(holderTag);
//				holder.contentTv.setOnClickListener(onClickListener);
//				holder.contentTv.setOnLongClickListener(onLongClickListener);
//				holder.tvFile.setVisibility(View.GONE);
//			}
//
//			getMsgStateResId(position, holder, detail, onClickListener);
//
//		}
	}

	@Override
	public int getChatViewType() {
		return ChattingRowType.FILE_ROW_RECEIVED.ordinal();
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, Message detail) {
		return false;
	}

}
