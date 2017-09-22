
package com.sanbafule.sharelock.chatting.util;


import com.sanbafule.sharelock.chatting.modle.FileMessageType;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.NotificationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
/**
 * @author Jorstin Chan@容联•云通讯
 * @date 2014-12-11
 * @version 4.0
 */
public class ChattingsRowUtils {

	/**
	 *
	 * @param shareLockMessage
	 * @return
	 */
	public static int getChattingMessageType(ShareLockMessage shareLockMessage) {
		Message message=shareLockMessage.getMessage();
		MessageContent messageContent=message.getContent();
		if(messageContent instanceof  TextMessage){
			return 1001;
		}else if(messageContent instanceof VoiceMessage){
			return 1002;
		}else if(messageContent instanceof ImageMessage){
			return 1003;
		}else if(messageContent instanceof FileMessage){
			if(((FileMessage) messageContent).getType().equals(FileMessageType.GIF.toString())){
				return 1006;
			}else if (((FileMessage) messageContent).getType().equals(FileMessageType.VIDEO.toString())) {
				return 10014;
			}
			return 1004;
		}else if(messageContent instanceof LocationMessage){
			return 1005;
		}else if (message.getConversationType() == Conversation.ConversationType.GROUP &&
				messageContent instanceof InformationNotificationMessage) {
			//群组聊天中的小灰条
			return 186;
		} else if (message.getConversationType() == Conversation.ConversationType.PRIVATE &&
				messageContent instanceof CommandNotificationMessage) {
			//删除好友 的小灰条
			return 186;
		}else if(messageContent instanceof NotificationMessage){
			return 1006;
		}else if(messageContent instanceof ContactNotificationMessage){
  			return 1007;
		}else if(messageContent instanceof RichContentMessage){
           return 1008;
		} else if (messageContent instanceof RecallNotificationMessage) {
			return 186;
		}
		return 1001;
	}
}
