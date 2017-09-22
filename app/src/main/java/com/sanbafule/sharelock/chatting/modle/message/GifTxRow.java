package com.sanbafule.sharelock.chatting.modle.message;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.GifRowHolder;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.FileUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;

import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/7 16:44
 * cd : 三八妇乐
 * 描述：
 */
public class GifTxRow extends BaseChattingRow {
    public GifTxRow(int type) {
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

        int i = DensityUtil.dip2px(context, 150);

        File localFile = new File(FileUtil.getPhotoPathFromContentUri(context, message.getLocalPath()));
        if (localFile.exists()) {
            Glide.with(context).load(message.getLocalPath()).asGif().
                    error(R.drawable.ic_play_gif).
                    diskCacheStrategy(DiskCacheStrategy.SOURCE).
                    override(i, i).
                    into(holder.gifImageView);
        } else {
            Glide.with(context).load(message.getMediaUrl()).asGif().
                    error(R.drawable.ic_play_gif).
                    diskCacheStrategy(DiskCacheStrategy.SOURCE).
                    override(i, i).
                    into(holder.gifImageView);
        }
        getMsgStateResId(context, holder, detail);
//        if (detail.getMessage().getSentStatus() == Message.SentStatus.SENDING) {
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(150, 150);
//            holder.uploading_view.setLayoutParams(layoutParams);
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
//
//            }
//        });

        // 点击事件  预览大图
        holder.gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.showMessage("Gif动图的点击事件");
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
            convertView = new ChattingItemContainer(inflater, R.layout.item_chatting_gif_to);

            // use the view holder pattern to save of already looked up subviews
            GifRowHolder holder = new GifRowHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChattingRowType.GIF_ROW_TRANSMIT.ordinal();
    }
}
