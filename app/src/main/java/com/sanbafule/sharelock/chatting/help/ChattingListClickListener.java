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
package com.sanbafule.sharelock.chatting.help;

import android.view.View;

import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.adapter.ChattingAdapter;
import com.sanbafule.sharelock.chatting.modle.ViewHolderTag;

import java.io.File;

import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class ChattingListClickListener implements View.OnClickListener{

	/**聊天界面*/
	private GroupChattingActivity mContext;
	
	public ChattingListClickListener(GroupChattingActivity activity ) {
		mContext = activity;
	}
	
	@Override
	public void onClick(View v) {
		ViewHolderTag holder = (ViewHolderTag) v.getTag();
		final Message iMessage = holder.detail;
		
		switch (holder.type) {
			// 视频
		case ViewHolderTag.TagType.TAG_VIEW_FILE:
			break;

		case ViewHolderTag.TagType.TAG_VOICE:
			// 声音
			if(iMessage == null) {
				return ;
			}
//			//点击后更新为已读 添加的语音消息是已读
			if (iMessage.getReceivedStatus()== new Message.ReceivedStatus(1)){
//				IMessageSqlManager.updateMsgReadStatus(iMessage.getMsgId(),true);
			}
//
			MediaPlayTools instance = MediaPlayTools.getInstance();
			final ChattingAdapter adapterForce = mContext.getChattingAdapter();
			if(instance.isPlaying()) {
				instance.stop();
			}
			if(adapterForce.mVoicePosition == holder.position) {
				adapterForce.mVoicePosition = -1;
				adapterForce.notifyDataSetChanged();
				return ;
			}

			instance.setOnVoicePlayCompletionListener(new MediaPlayTools.OnVoicePlayCompletionListener() {

				@Override
				public void OnVoicePlayCompletion() {
					adapterForce.mVoicePosition = -1;
					adapterForce.notifyDataSetChanged();
				}
			});

			VoiceMessage message= (VoiceMessage) holder.detail.getContent();
             File  file =new File(message.getUri().getPath());
            String fileLocalPath=file.getAbsolutePath();
            if(file.exists()){
                boolean isEarpiece=true;
                instance.playVoice(fileLocalPath, isEarpiece);
                adapterForce.setVoicePosition(holder.position);
                adapterForce.notifyDataSetChanged();
            }

			break;
			
		case ViewHolderTag.TagType.TAG_VIEW_PICTURE:
//			// 图片
//            // 获取和这个人的所有的聊天图片
//
//            if (iMessage!=null){
//
//				SChattingHelp.showImageList(iMessage, mContext);
//            }


			break;
			
		case ViewHolderTag.TagType.TAG_RESEND_MSG :
			// 消息重发

//		    SChattingHelp.reSendMessage(iMessage,mContext.getChattingAdapter());
//			mContext.mChattingFragment.doResendMsgRetryTips(iMessage, holder.position);
			break;
		case ViewHolderTag.TagType.TAG_IM_LOCATION :
			// 位置
			
//			CCPAppManager.startShowBaiDuMapAction(mContext,iMessage);
			break;
		default:
			
			
			break;
		}
	}



}
