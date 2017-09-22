package com.sanbafule.sharelock.chatting.modle.message;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.GifRowHolder;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.FileUtil;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.File;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

import static com.sanbafule.sharelock.R.id.imageView;

/**
 * Administrator
 * 作者:Created by ShareLock on 2017/1/7 16:44
 * cd : 三八妇乐
 * 描述： 显示接受的Gif的Item条目
 */
public class GifRxRow extends BaseChattingRow {
    public GifRxRow(int type) {
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
        Glide.with(context).load(message.getMediaUrl()).asGif().
                error(R.drawable.ic_play_gif).
                diskCacheStrategy(DiskCacheStrategy.SOURCE).
                override(i,i).
                into(holder.gifImageView);

        // 点击事件  预览大图
        holder.gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        // 长按事件  撤回消息等
        holder.gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((GroupChattingActivity)context).handLeftFile(detail);
                return true;
            }
        });

    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null || convertView.getTag() == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.item_chatting_gif_from);

            // use the view holder pattern to save of already looked up subviews
            GifRowHolder holder = new GifRowHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;

    }

    @Override
    public int getChatViewType() {
        return ChattingRowType.GIF_ROW_RECEIVED.ordinal();
    }
}
