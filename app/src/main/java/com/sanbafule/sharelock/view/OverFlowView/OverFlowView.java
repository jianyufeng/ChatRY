package com.sanbafule.sharelock.view.OverFlowView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.DividerItemDecoration;

/**
 * Created by Administrator on 2016/10/8.
 */
public class OverFlowView extends PopupWindow {

    private RecyclerView recyclerView;
    private View view;
    public OverFlowView(Context context) {
        this(context, null);
    }

    public OverFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView( Context context) {
        view=LayoutInflater.from(context).inflate(R.layout.overflowview,null);
        recyclerView= (RecyclerView) view.findViewById(R.id.overflow_RecyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(context,1));

    }

}
