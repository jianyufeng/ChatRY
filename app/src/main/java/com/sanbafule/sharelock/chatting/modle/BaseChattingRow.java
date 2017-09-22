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
package com.sanbafule.sharelock.chatting.modle;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.user.UserInfoActivity;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.message.IChattingRow;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.ToastUtil;

import java.util.HashMap;

import io.rong.imlib.model.Message;

import static io.rong.imlib.model.Message.MessageDirection;
import static io.rong.imlib.model.Message.SentStatus;

/**
 *
 */
public abstract class BaseChattingRow implements IChattingRow {

    public static final String TAG = "BaseChattingRow";
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    public int mRowType;

    public BaseChattingRow(int type) {
        mRowType = type;
    }

    @Override
    public int getChatViewType() {
        return 0;
    }

    /**
     * 处理消息的发送状态设置
     *

     * @param holder   消息ViewHolder
     */
    protected static void getMsgStateResId(final Context context, BaseHolder holder, final ShareLockMessage message) {
        Message msg = message.getMessage();
        if (msg != null && msg.getMessageDirection() == MessageDirection.SEND) {
            SentStatus msgStatus = msg.getSentStatus();
            if (msgStatus == SentStatus.FAILED) {
                holder.getUploadState().setImageResource(R.drawable.msg_state_failed_resend);
                holder.getUploadState().setVisibility(View.VISIBLE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }
            } else if (msgStatus == SentStatus.SENT) {
                holder.getUploadState().setImageResource(0);
                holder.getUploadState().setVisibility(View.GONE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }

            } else if (msgStatus == SentStatus.SENDING) {
                holder.getUploadState().setImageResource(0);
                holder.getUploadState().setVisibility(View.GONE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.VISIBLE);
                }
            } else {
                holder.getUploadState().setImageResource(0);
                holder.getUploadState().setVisibility(View.GONE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }

            }
            if (holder.getUploadState() != null) {
                holder.getUploadState().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SChattingHelp.reSendMessage(message, ((GroupChattingActivity) context).getChattingAdapter());
                    }
                });
            }

        }
    }


    /**
     * 处理消息的发送状态设置
     *
     * @param position 消息的列表所在位置
     * @param holder   消息ViewHolder
     */
    protected static void getMsgStateResId(int position, BaseHolder holder, Message msg) {
        if (msg != null && msg.getMessageDirection() == MessageDirection.SEND) {
            SentStatus msgStatus = msg.getSentStatus();
            if (msgStatus == SentStatus.FAILED) {
                holder.getUploadState().setImageResource(R.drawable.msg_state_failed_resend);
                holder.getUploadState().setVisibility(View.VISIBLE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }
            } else if (msgStatus == SentStatus.SENT || msgStatus == SentStatus.RECEIVED) {
                holder.getUploadState().setImageResource(0);
                holder.getUploadState().setVisibility(View.GONE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }

            } else if (msgStatus == SentStatus.SENDING) {
                holder.getUploadState().setImageResource(0);
                holder.getUploadState().setVisibility(View.GONE);
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.VISIBLE);
                }
            } else {
                if (holder.getUploadProgressBar() != null) {
                    holder.getUploadProgressBar().setVisibility(View.GONE);
                }

            }

            ViewHolderTag holderTag = ViewHolderTag.createTag(msg, ViewHolderTag.TagType.TAG_RESEND_MSG, position);
            holder.getUploadState().setTag(holderTag);
            holder.getUploadState().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    /**
     * @param contextMenu
     * @param targetView
     * @param detail
     * @return
     */
    public abstract boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, Message detail);


    /**
     * @param baseHolder
     * @param displayName
     */
    public static void setDisplayName(BaseHolder baseHolder, String displayName) {
        if (baseHolder == null || baseHolder.getChattingUser() == null) {
            return;
        }

        if (TextUtils.isEmpty(displayName)) {
            baseHolder.getChattingUser().setVisibility(View.GONE);
            return;
        }
        baseHolder.getChattingUser().setVisibility(View.VISIBLE);
        baseHolder.getChattingUser().setText(displayName);
    }

    protected abstract void buildChattingData(Context context, BaseHolder baseHolder, ShareLockMessage detail, int position);

    @Override
    public void buildChattingBaseData(Context context, BaseHolder baseHolder, ShareLockMessage message, int position) {
        Message detail = message.getMessage();
        // 处理其他使用逻辑
        buildChattingData(context, baseHolder, message, position);
        setContactPhoto(context, baseHolder, detail);
        // 设置聊天的名称

        if (detail.getMessageDirection() == MessageDirection.RECEIVE) {
            ContactInfo contact = ContactInfoSqlManager.getContactInfoFormuserName(detail.getSenderUserId());
            if (contact != null) {
                setDisplayName(baseHolder, contact.getName());
            }
            setContactPhotoClickListener(context, baseHolder, detail);
        }
        if (detail.getMessageDirection() == MessageDirection.SEND) {
            setMyName(baseHolder, ShareLockManager.getInstance().getClentUser());
            setMyHeaderClickListener(context, baseHolder, detail);
        }
    }

    private void setMyHeaderClickListener(final Context context, BaseHolder baseHolder, final Message detail) {
        if (baseHolder.getChattingAvatar() != null && detail != null) {
            /**
             * 点击头像的事件
             */
            baseHolder.getChattingAvatar().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(SString.TARGETNAME, detail.getSenderUserId());
                    context.startActivity(intent);
                }
            });

            /***
             *
             * 群组头像的长按事件
             */
            baseHolder.getChattingAvatar().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });
        }

    }

    ;

    /**
     * 设置自己的名字
     *
     * @param baseHolder
     * @param clientUser
     */
    private void setMyName(BaseHolder baseHolder, ClientUser clientUser) {
        if (baseHolder == null || baseHolder.getChattingUser() == null) {
            return;
        }
        if (MyString.hasData(clientUser.getU_nickname())) {
            baseHolder.getChattingUser().setVisibility(View.VISIBLE);
            baseHolder.getChattingUser().setText(clientUser.getU_nickname());
        } else if (MyString.hasData(clientUser.getU_username())) {
            baseHolder.getChattingUser().setVisibility(View.VISIBLE);
            baseHolder.getChattingUser().setText(clientUser.getU_username());
        } else {
            baseHolder.getChattingUser().setVisibility(View.GONE);
        }
    }

    private void setContactPhotoClickListener(final Context context, BaseHolder baseHolder, final Message detail) {
        if (baseHolder.getChattingAvatar() != null && detail != null) {
            /**
             * 点击头像的事件
             */
            baseHolder.getChattingAvatar().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //是群组
                    if (ShareLockManager.isGroupChat(detail.getTargetId())) {
                        ShareLockManager.startGpMbDtlActy(context, detail.getTargetId(), detail.getSenderUserId());
                    } else {
                        ShareLockManager.startContactInfoActivity(context, detail.getTargetId());
                    }
                }
            });

            /***
             *
             * 群组头像的长按事件
             */
            baseHolder.getChattingAvatar().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //  聊天出现的@
                    if (ShareLockManager.isGroupChat(detail.getTargetId()) && !GroupChattingActivity.mInstance.mAtsomeone && !detail.getSenderUserId().equals(ShareLockManager.getInstance().getUserName())) {
                        GroupChattingActivity.mInstance.mAtsomeone = true;
                        // 群组
                        GroupChattingActivity.mInstance.setLastText(GroupChattingActivity.mInstance.getLastText() + "@" + detail.getSenderUserId() + (char) (8197));
                        GroupChattingActivity.mInstance.atList.add(detail.getSenderUserId());
                        GroupChattingActivity.mInstance.ekBar.setOpenSoft();
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GroupChattingActivity.mInstance.mAtsomeone = false;
                            }
                        }, 1000L);
                    }
                    ToastUtil.showMessage(detail.getSenderUserId());
                    return true;
                }
            });
        }
    }


    /**
     * 设置聊天用户头像
     *
     * @param baseHolder
     * @param
     */
    private void setContactPhoto(Context context, BaseHolder baseHolder, Message message) {
        if (baseHolder.getChattingAvatar() == null) {
            return;
        }
        /**
         * 设置自己的头像  待改！！！！！！！！！！！
         */
        String u_header = null;
        if (message.getMessageDirection() == MessageDirection.SEND) {
            ClientUser clientUser = ShareLockManager.getInstance().getClentUser();
            u_header = clientUser.getU_header();
        }
        /**
         * 设置对方的聊天头像
         */
        if (message.getMessageDirection() == MessageDirection.RECEIVE) {

            if (baseHolder.getChattingAvatar() != null) {
                try {
                    String fiendName = message.getSenderUserId();
                    if (TextUtils.isEmpty(fiendName)) {
                        return;
                    }
                    ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(fiendName);
                    if (info != null) {
                        u_header = info.getU_header();
                    } else {
                        u_header = null;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    u_header = null;
                }
            }

        }

        ImageBiz.showImage(context, baseHolder.getChattingAvatar(), ShareLockManager.getImgUrl(u_header), R.drawable.icon_touxiang_persion_gray);


    }


}
