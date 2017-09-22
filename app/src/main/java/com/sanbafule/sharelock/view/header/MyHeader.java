package com.sanbafule.sharelock.view.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.sanbafule.sharelock.R;



public class MyHeader  extends LinearLayout implements PtrUIHandler{
	
	public MyHeader(Context context) {
		super(context);
		initview();
	}

	private void initview() {
		LayoutInflater.from(getContext()).inflate(R.layout.my_header, this);
	
	}

	@Override
	public void onUIReset(PtrFrameLayout frame) {

		
	}

	@Override
	public void onUIRefreshPrepare(PtrFrameLayout frame) {

		
	}

	@Override
	public void onUIRefreshBegin(PtrFrameLayout frame) {

		
	}

	@Override
	public void onUIRefreshComplete(PtrFrameLayout frame) {

		
	}

	@Override
	public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch,
			byte status, PtrIndicator ptrIndicator) {

		
	}

}
