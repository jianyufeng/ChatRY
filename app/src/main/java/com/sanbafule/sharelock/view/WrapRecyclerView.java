package com.sanbafule.sharelock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.sanbafule.sharelock.adapter.RecyclerWrapAdapter;

import java.util.ArrayList;

/**
 * ShareLock
 * 能添加头部和底部的RecyclerView
 * date:2016-11-4
 */
public class WrapRecyclerView extends RecyclerView {
    private ArrayList<View> mHeaderViews ;

    private ArrayList<View> mFootViews ;

    private Adapter mAdapter ;

    public WrapRecyclerView(Context context) {
        this(context,null);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHeaderViews = new ArrayList<>();
        mFootViews = new ArrayList<>() ;
    }

    public void addHeaderView(View view){
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFootView(View view){
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (mHeaderViews.isEmpty()&&mFootViews.isEmpty()){
            super.setAdapter(adapter);
        }else {
            adapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,adapter) ;
            super.setAdapter(adapter);
        }
        mAdapter = adapter ;
    }

}
