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
import android.view.LayoutInflater;
import android.view.View;

import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;

import io.rong.imlib.model.Message;


public interface IChattingRow {

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param convertView
     * @return
     */
    View buildChatView(LayoutInflater inflater, View convertView);

    /**
     *
     * @param context
     * @param detail
     */
    void buildChattingBaseData(Context context, BaseHolder baseHolder, ShareLockMessage detail, int position);

    /**
     * @return
     */
    int getChatViewType();

}
