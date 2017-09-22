package com.sanbafule.sharelock.chatting.modle.message;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.ChattingItemContainer;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.holder.LocationViewHolder;

import io.rong.imlib.model.Message;


public class LocationTxRow extends BaseChattingRow {

	public LocationTxRow(int type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
		 if (convertView == null ) {
	            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_location_to);
	            LocationViewHolder holder = new LocationViewHolder(mRowType);
	            convertView.setTag(holder.initBaseHolder(convertView, true));
	        } 
			return convertView;
	}

	@Override
	public int getChatViewType() {
		// TODO Auto-generated method stub
		return ChattingRowType.LOCATION_ROW_TRANSMIT.ordinal();
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, Message detail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void buildChattingData(Context context, BaseHolder baseHolder,
									 ShareLockMessage detail, int position) {

//		LocationViewHolder holder = (LocationViewHolder) baseHolder;
//		ECMessage message = detail;
//		if(message != null) {
//			ViewHolderTag holderTag = ViewHolderTag.createTag(detail,
//					ViewHolderTag.TagType.TAG_IM_LOCATION, position);
//			ECLocationMessageBody textBody = (ECLocationMessageBody) message.getBody();
//			holder.descTextView.setText(textBody.getTitle());
//			OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
//			View.OnLongClickListener onLongClickListener=((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnLongClickListenner();
//			getMsgStateResId(position, holder, detail, onClickListener);
//			holder.relativeLayout.setTag(holderTag);
//			holder.relativeLayout.setOnClickListener(onClickListener);
//			holder.relativeLayout.setOnLongClickListener(onLongClickListener);
//		}
		
	}

}
