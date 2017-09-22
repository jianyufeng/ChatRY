/**
 * 
 */
package com.sanbafule.sharelock.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.view.header.MyHeader;
import com.sanbafule.sharelock.view.header.PtrDefaultHandler;
import com.sanbafule.sharelock.view.header.PtrFrameLayout;
import com.sanbafule.sharelock.view.header.PtrHandler;

import java.util.ArrayList;
import java.util.List;


public class MyHeaderActivity extends BaseActivity {

	
	private PtrFrameLayout mPtrFrame;
	private ListView mListView;
	private ArrayAdapter<String> adapter;
	
	private List<String> listStr;
	private int refreshCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPtrFrame = (PtrFrameLayout)findViewById(R.id.rotate_header_list_view_frame);
		mListView = (ListView) findViewById(R.id.listView1);
		
		listStr = new ArrayList<String>();
        listStr.add("a");
        listStr.add("b");
        listStr.add("c");
        listStr.add("d");
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStr);
		mListView.setAdapter(adapter);
		
		MyHeader header = new MyHeader(this);
		mPtrFrame.setHeaderView(header);
		mPtrFrame.addPtrUIHandler(header);
		
		//mPtrFrame.setRatioOfHeaderHeightToRefresh(180f/1134);
		mPtrFrame.setOffsetToRefresh(DensityUtil.dip2px(70F));	//
		mPtrFrame.setOffsetToKeepHeaderWhileLoading(DensityUtil.dip2px(70F));
		

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

            }
        });
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_header;
	}
}
