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
import android.view.View.OnLongClickListener;

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
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * 收图片
 * ShareLock
 */
public class ImageRxRow extends BaseChattingRow {

    public ImageRxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater,
                    R.layout.chatting_item_from_picture);
            ImageRowViewHolder holder = new ImageRowViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));

        }
        return convertView;
    }

    @Override
    public void buildChattingData(final Context context, BaseHolder baseHolder,
                                  final ShareLockMessage detail, int position) {

        if (detail == null || detail.getMessage() == null) {
            return;
        }

        ImageMessage message = (ImageMessage) detail.getMessage().getContent();
        final ImageRowViewHolder holder = (ImageRowViewHolder) baseHolder;
        // 获取本地路径
        int width, height;
        File thumbFile = null;
        File localFile = null;
        if (MyString.hasData(message.getThumUri().toString())) {
            thumbFile = new File(FileUtil.getPhotoPathFromContentUri(context, message.getThumUri()));
        }

        if (message.getLocalUri()!=null&&MyString.hasData(message.getLocalUri().toString())) {
            localFile = new File(FileUtil.getPhotoPathFromContentUri(context, message.getLocalUri()));

        }
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

            holder.chattingContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail != null) {
                        SChattingHelp.showImageList(detail.getMessage(), (Activity) context);
                    }
                }
            });
            final File finalLocalFile = localFile;
            holder.chattingContentIv.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (finalLocalFile.exists()) {
                        ((GroupChattingActivity) context).handLeftImageShowTranspond(detail);
                    } else {
                        ((GroupChattingActivity) context).handLeftImage(detail);

                    }
                    return true;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getChatViewType() {

        return ChattingRowType.IMAGE_ROW_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, Message detail) {
        return false;
    }
}
