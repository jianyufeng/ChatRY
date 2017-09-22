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

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.ImageRowViewHolder;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * 发图片
 * ShareLock
 */
public class ImageTxRow extends BaseChattingRow {

    private static final String TAG = "ImageTxRow";

    public ImageTxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater,
                    R.layout.chatting_item_to_picture);
            ImageRowViewHolder holder = new ImageRowViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));

        }
        return convertView;
    }

    @Override
    public void buildChattingData(final Context context, BaseHolder baseHolder,
                                  final ShareLockMessage detail, int position) {

        if (detail == null || detail.getMessage() == null) {
            return;
        }
        final ImageRowViewHolder holder = (ImageRowViewHolder) baseHolder;
        final ImageMessage message = (ImageMessage) detail.getMessage().getContent();

        int width, height;
        final File localFile = new File(FileUtil.getPhotoPathFromContentUri(context, message.getLocalUri()));
        File thumbFile = new File(FileUtil.getPhotoPathFromContentUri(context, message.getThumUri()));
        String extra = message.getExtra();

        try {
            JSONObject object = new JSONObject(extra);
            width = object.getInt(SString.WIDTH);
            height = object.getInt(SString.HEIGHT);
            if (thumbFile.exists()) {
                Glide.
                        with(context).
                        load(message.getThumUri()).
                        error(R.drawable.icon_touxiang_persion_gray).
                        override(width, height).
                        fitCenter().
                        crossFade(300).
                        into(holder.chattingContentIv);

            } else {
                Glide.
                        with(context).
                        load(message.getRemoteUri()).
                        error(R.drawable.icon_touxiang_persion_gray).
                        override(width, height).
                        fitCenter().
                        crossFade(300).
                        into(holder.chattingContentIv);
            }

            getMsgStateResId(context, holder, detail);
//            if (detail.getMessage().getSentStatus() == Message.SentStatus.SENDING) {
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
//                holder.uploading_Layout.setLayoutParams(layoutParams);
//                holder.uploading_Layout.setVisibility(View.VISIBLE);
//                holder.chattingStateIv.setVisibility(View.GONE);
//                holder.uploadingText.setText(String.valueOf(detail.getProgress()));
////                Log.e(TAG, "buildChattingData: "+detail.getProgress() );
//            } else if (detail.getMessage().getSentStatus() == Message.SentStatus.SENT) {
//                holder.uploading_Layout.setVisibility(View.GONE);
//                holder.chattingStateIv.setVisibility(View.GONE);
//            } else if (detail.getMessage().getSentStatus() == Message.SentStatus.FAILED) {
//                holder.chattingStateIv.setVisibility(View.VISIBLE);
//                holder.chattingStateIv.setImageResource(R.drawable.msg_state_fail_resend);
//                holder.uploading_Layout.setVisibility(View.GONE);
//            }
            // 点击事件  预览大图
            holder.chattingContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail != null) {
                        SChattingHelp.showImageList(detail.getMessage(), (Activity) context);
                    }
                }
            });

            // 长按事件  撤回消息等
            holder.chattingContentIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (localFile.exists()) {
                        ((GroupChattingActivity) context).handRightImageShowTranspond(detail);

                    } else {
                        ((GroupChattingActivity) context).handRightImage(detail);
                    }
                    return true;
                }
            });

//            // 点击消息重发
//            holder.getUploadState().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SChattingHelp.reSendMessage(detail,((GroupChattingActivity) context).getChattingAdapter());
//
//                }
//            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getChatViewType() {
        return ChattingRowType.IMAGE_ROW_TRANSMIT.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, Message detail) {
        return false;
    }


}
