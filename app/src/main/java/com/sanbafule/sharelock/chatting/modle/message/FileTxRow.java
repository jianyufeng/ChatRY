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
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
 * 文件发送
 * @author ShareLock
 * @date 2016-10-17
 * @version 1.0
 */
public class FileTxRow extends BaseChattingRow {

	public FileTxRow(int type) {
		super(type);
	}

	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
		// we have a don't have a converView so we'll have to create a new one
		if (convertView == null || convertView.getTag() == null) {
			convertView = new ChattingItemContainer(inflater,
					R.layout.chatting_item_file_to);

			// use the view holder pattern to save of already looked up subviews
			FileRowViewHolder holder = new FileRowViewHolder(mRowType);
			convertView.setTag(holder.initBaseHolder(convertView, false));
		}
		return convertView;
	}

	@Override
	public void buildChattingData(Context context, BaseHolder baseHolder,
			ShareLockMessage detail, int position) {

		if (detail == null || detail.getMessage() == null) {
			return;
		}
		final GifRowHolder holder = (GifRowHolder) baseHolder;
		final FileMessage message = (FileMessage) detail.getMessage().getContent();

		Glide.with(context).load(message.getMediaUrl()).asGif().into(holder.gifImageView);
//
//		FileRowViewHolder holder = (FileRowViewHolder) baseHolder;
//		ViewHolderTag holderTag = ViewHolderTag.createTag(detail,
//				ViewHolderTag.TagType.TAG_VIEW_FILE, position);
//		OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment
//				.getChattingAdapter().getOnClickListener();
//		View.OnLongClickListener onLongClickListener = ((ChattingActivity) context).mChattingFragment
//				.getChattingAdapter().getOnLongClickListenner();
//		if (detail != null) {
//			ECMessage message = detail;
//			ECFileMessageBody fileBody = (ECFileMessageBody) message.getBody();
//			String localPath = fileBody.getLocalUrl();
//			String fileName = "";
//
//			if (message.getType() == com.yuntongxun.ecsdk.ECMessage.Type.FILE) {
//				fileName = fileBody.getFileName();
//				holder.contentTv.setText(fileName);
//				holder.contentTv.setOnLongClickListener(onLongClickListener);
//			}else if (message.getType() == com.yuntongxun.ecsdk.ECMessage.Type.VIDEO) {
//				ECVideoMessageBody videoFileBody = (ECVideoMessageBody) message
//						.getBody();
//				String videoPath = videoFileBody.getLocalUrl();
//
//				fileName = videoPath;
//			}
//
//			if ("mp4".equals(FileUtils.getFileExt(fileName))) {
//				holder.contentTv.setVisibility(View.GONE);
//				holder.contentTv.setTag(null);
//				holder.contentTv.setOnClickListener(null);
//				holder.fl.setVisibility(View.VISIBLE);
//
//				holder.ivVideoMp4.setVisibility(View.VISIBLE);
//				holder.ivVideoMp4.setTag(holderTag);
//				holder.ivVideoMp4.setOnLongClickListener(onLongClickListener);
//				holder.buPlayVideo.setOnClickListener(onClickListener);
//				holder.buPlayVideo.setTag(holderTag);
//				Bitmap createVideoThumbnail=null;
//				if(fileBody.getLocalUrl()!=null){
//					createVideoThumbnail = FileUtils.createVideoThumbnail(fileBody.getLocalUrl());
//				}
//			if(createVideoThumbnail!=null){
//				createVideoThumbnail=decodeBitmap(createVideoThumbnail,400,247);
//			}
//				if (createVideoThumbnail != null) {
//					holder.ivVideoMp4.setImageBitmap(createVideoThumbnail);
//
//				}else {
//					holder.ivVideoMp4.setImageResource(R.drawable.delete_message);
//				}
//			} else {
//				holder.contentTv.setVisibility(View.VISIBLE);
//				holder.ivVideoMp4.setVisibility(View.GONE);
//				holder.fl.setVisibility(View.GONE);
//				holder.buPlayVideo.setTag(null);
//				holder.buPlayVideo.setOnClickListener(null);
//				holder.contentTv.setTag(holderTag);
//				holder.contentTv.setOnClickListener(onClickListener);
//			}
//			getMsgStateResId(position, holder, detail, onClickListener);
//
//		}
	}

	public static Bitmap decodeBitmap(Bitmap bm, int displayWidth, int displayHeight) {
		Bitmap newbm = null;
// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) displayWidth) / width;
		float scaleHeight = ((float) displayHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片   www.2cto.com
		return newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	}

	@Override
	public int getChatViewType() {

		return ChattingRowType.FILE_ROW_TRANSMIT.ordinal();
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, Message detail) {
		// TODO Auto-generated method stub
		return false;
	}

}
