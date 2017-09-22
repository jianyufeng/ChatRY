package com.sanbafule.sharelock.chatting.modle.conversation.holder;

import android.view.View;
import android.widget.ImageView;

import com.sanbafule.sharelock.R;

/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/20 10:20
 * cd : 三八妇乐
 * 描述：
 */
public class PrivateHolder extends BaseConversationHolder {

    public ImageView mConversationSentStatus;
//    public TextView mNormalRead;
    public PrivateHolder(View itemView) {
        super(itemView);
        mConversationSentStatus= (ImageView) itemView.findViewById(R.id.conversion_SentStatus);
//        mNormalRead= (TextView) itemView.findViewById(R.id.read);
    }

//    @Override
//    public RecyclerView.ViewHolder getViewHolder(int type) {
//        if (type == 1) {
//            return this;
//        }
//        return null;
//    }
}
