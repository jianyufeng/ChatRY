package com.sanbafule.sharelock.chatting.modle.message;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.activity.play.PlayVideoActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.GifRowHolder;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.ToastUtil;

import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;
import io.rong.message.utils.BitmapUtil;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/7 16:44
 * cd : 三八妇乐
 * 描述：
 */
public class VideoTxRow extends BaseChattingRow {
    public VideoTxRow(int type) {
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
        final GifRowHolder holder = (GifRowHolder) baseHolder;
        final FileMessage message = (FileMessage) detail.getMessage().getContent();
        //设置第一帧图像
        holder.gifImageView.setImageBitmap(BitmapUtil.getBitmapFromBase64(message.getExtra()));

        getMsgStateResId(context, holder, detail);
//        if (detail.getMessage().getSentStatus() == Message.SentStatus.SENDING) {
//            holder.uploading_view.setVisibility(View.VISIBLE);
//            holder.uploadingTv.setText(String.valueOf(detail.getProgress()));
//        } else if (detail.getMessage().getSentStatus() == Message.SentStatus.SENT) {
//            holder.uploading_view.setVisibility(View.GONE);
//            holder.chattingStateIv.setVisibility(View.GONE);
//        } else if (detail.getMessage().getSentStatus() == Message.SentStatus.FAILED) {
//            holder.uploading_view.setVisibility(View.GONE);
//            holder.chattingStateIv.setVisibility(View.VISIBLE);
//            holder.chattingStateIv.setImageResource(R.drawable.msg_state_fail_resend);
//        }
//
//        // 点击消息重发
//        holder.chattingStateIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SChattingHelp.reSendMessage(detail, ((GroupChattingActivity) context).getChattingAdapter());
//            }
//        });

        // 点击事件  预览大图
        holder.gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PlayVideoActivity.class)
                        .putExtra(SString.TYPE, detail.getMessage()));
            }
        });
        // 长按事件  撤回消息等
        holder.gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((GroupChattingActivity) context).handRightFile(detail);
                return false;
            }
        });


    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null || convertView.getTag() == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.item_chatting_video_to);

            // use the view holder pattern to save of already looked up subviews
            GifRowHolder holder = new GifRowHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChattingRowType.VIDEO_ROW_TRANSMIT.ordinal();
    }
}
