package com.sanbafule.sharelock.view.customRecycleView;

/**
 * Created by Administrator on 2016/11/16.
 * Adapter拆分为两个抽象类： AbsAdapter 与 BaseAdapter ，其中：
 * AbsAdapter ：封装了和ViewHolder和HeaderView，FooterView相关的方法。
 * <p>
 * BaseAdapter ：继承AbsAdapter,封装了数据相关的方法。
 * <p>
 * 各自聚焦于不同的方面，方面日后扩展。
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView.Adapter的扩展,包含headerView/footerView等
 * Created by 简玉锋 on 2016/7/13.
 */

public abstract class RecycleViewAbsAdapter<M, VH extends RecycleViewBaseHolder> extends RecyclerView.Adapter<RecycleViewBaseHolder> {

    private static final String TAG = "AbsAdapter";

    protected static final int VIEW_TYPE_HEADER = 1024;
    public static final int VIEW_TYPE_FOOTER = 1025;

    protected View headerView;
    protected View footerView;

    protected Context context;
    private RecyclerViewClick.Click click;

    public RecycleViewAbsAdapter(Context context) {
        this.context = context;
    }

    public void setItemClickAndLongClick(RecyclerViewClick.Click click) {
        this.click = click;
    }

    @Override
    public final RecycleViewBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new RecycleViewBaseHolder(headerView);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new RecycleViewBaseHolder(footerView);
        } else {
            return createCustomViewHolder(parent, viewType);
        }
    }

    /**
     * 创建自定义的ViewHolder
     *
     * @param parent 父类容器
     * @param viewType view类型{@link #getItemViewType(int)}
     * @return ViewHolder
     */
    public abstract VH createCustomViewHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecycleViewBaseHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
            case VIEW_TYPE_FOOTER:
                break;
            default:
                bindCustomViewHolder((VH) holder, position);
                if (click!=null){
                    //设置点击事件
                  new RecyclerViewClick(((VH) holder).itemView, position, click);
                }
                break;
        }
    }

    /**
     * 绑定自定义的ViewHolder
     *
     * @param holder ViewHolder
     * @param position 位置
     */
    public abstract void bindCustomViewHolder(VH holder, int position);

    /**
     * 添加HeaderView
     *
     * @param headerView 顶部View对象
     */
    public void addHeaderView(View headerView) {
        if (headerView == null) {
            Log.w(TAG, "add the header view is null");
            return;
        }
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    /**
     * 移除HeaderView
     */
    public void removeHeaderView() {
        if (headerView != null) {
            headerView = null;
            notifyDataSetChanged();
        }
    }

    /**
     * 添加FooterView
     *
     * @param footerView View对象
     */
    public void addFooterView(View footerView) {
        if (footerView == null) {
            Log.w(TAG, "add the footer view is null");
            return;
        }
        this.footerView = footerView;
        notifyDataSetChanged();
    }

    /**
     * 移除FooterView
     */
    public void removeFooterView() {
        if (footerView != null) {
            footerView = null;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取附加View的数量,包括HeaderView和FooterView
     *
     * @return 数量
     */
    public int getExtraViewCount() {
        int extraViewCount = 0;
        if (headerView != null) {
            extraViewCount++;
        }
        if (footerView != null) {
            extraViewCount++;
        }
        return extraViewCount;
    }

    /**
     * 获取顶部附加View数量,即HeaderView数量
     * @return 数量
     */
    public int getHeaderExtraViewCount() {
        return headerView == null ? 0 : 1;
    }

    /**
     * 获取底部附加View数量,即FooterView数量
     * @return 数量, 0或1
     */
    public int getFooterExtraViewCount() {
        return footerView == null ? 0 : 1;
    }

    @Override
    public abstract long getItemId(int position);
}
