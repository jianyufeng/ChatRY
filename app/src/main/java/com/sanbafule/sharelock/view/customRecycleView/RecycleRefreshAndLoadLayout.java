package com.sanbafule.sharelock.view.customRecycleView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;

/**
 * Created by 简玉锋 on 16-11-18.
 */
public class RecycleRefreshAndLoadLayout extends SwipeRefreshLayout {
    private View loadingview;//加载更多
    private RecyclerView recyclerView;
    private int tauchSlop;//滚动的标准最小值

    private int downY, lastY;
    private boolean isLoading, isUpPull, isToBootom;

    public RecycleRefreshAndLoadLayout(Context context) {
        this(context, null);

    }

    public RecycleRefreshAndLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取滚动值
        tauchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //获取底部视图
        loadingview = LayoutInflater.from(context).inflate(R.layout.footer_load, null);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                lastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                isUpPull = (downY - lastY >= tauchSlop);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (recyclerView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                if (recyclerView==null){
                    findRecycleView(getChildAt(i));
                }
            }
        }
    }
    //查找之前 先判断是否已经查找到了
    void findRecycleView(View view) {
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            //设置滑动监听
            recyclerView.addOnScrollListener(new recycleScrollListener());
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (recyclerView==null){
                    findRecycleView(viewGroup.getChildAt(i));
                }
            }

        }
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (recyclerView != null) {
            RecycleViewAbsAdapter adapter = (RecycleViewAbsAdapter) recyclerView.getAdapter();
            if (loadingview != null) {
                if (isLoading) {
                    adapter.addFooterView(loadingview);//开始加载
                } else {
                    adapter.removeFooterView();//加载完成
                }
            }

        }

    }

    private OnLoadListener loadListener;

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface OnLoadListener {
        void onLoad();
    }

    class recycleScrollListener extends RecyclerView.OnScrollListener {


        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (isToBootom && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoading && isUpPull) {
//            //加载下页数据
                if (loadListener != null) {
                    setLoading(true);
                    loadListener.onLoad();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                if (linearManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 1) {
                    isToBootom = true;
                } else {
                    isToBootom = false;
                }
            }

        }
    }
}
