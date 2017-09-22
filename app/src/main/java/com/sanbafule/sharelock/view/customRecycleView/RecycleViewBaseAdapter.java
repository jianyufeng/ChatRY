package com.sanbafule.sharelock.view.customRecycleView;

/**
 * Created by Administrator on 2016/11/16.
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础的Adapter
 * <p>
 * Created by DevWiki on 2016/7/13.
 */

public abstract class RecycleViewBaseAdapter<M, VH extends RecycleViewBaseHolder> extends RecycleViewAbsAdapter<M, VH> {

    //数据集合
    private List<M> dataList;

    //不传集合 默认是空
    public RecycleViewBaseAdapter(Context context) {
        super(context);
        this.dataList = new ArrayList<>();
    }

    //传数据  统一使用本类中的数据引用
    public RecycleViewBaseAdapter(Context context, List<M> list) {
        super(context);
        this.dataList = new ArrayList<>();
        this.dataList.addAll(list);
    }

//    //绑定数据   进行数据转换
//    @Override
//    public void bindCustomViewHolder(VH holder, int position) {
//        bindCustomDataViewHolder(holder, dataList.get(position - getHeaderExtraViewCount()), position);
//    }

    //使用此方法进行数据绑定
//    public abstract void bindCustomDataViewHolder(VH holder, M m, int position);

    /**
     * 填充数据,此操作会清除原来的数据
     *
     * @param list 要填充的数据
     * @return true:填充成功并调用刷新数据
     */
    public boolean fillList(List<M> list) {
        dataList.clear();
        boolean result = dataList.addAll(list);
        if (result) {
            notifyDataSetChanged();
        }
        return result;
    }

    /**
     * 追加一条数据
     *
     * @param data 要追加的数据
     * @return true:追加成功并刷新界面
     */
    public boolean appendItem(M data) {
        boolean result = dataList.add(data);
        if (result) {
            if (getHeaderExtraViewCount() == 0) {
                notifyItemInserted(dataList.size() - 1);
            } else {
                notifyItemInserted(dataList.size());
            }
        }
        return result;
    }

    /**
     * 追加集合数据
     *
     * @param list 要追加的集合数据
     * @return 追加成功并刷新
     */
    public boolean appendList(List<M> list) {
        boolean result = dataList.addAll(list);
        if (result) {
            notifyDataSetChanged();
        }
        return result;
    }

    /**
     * 在最顶部前置数据
     *
     * @param data 要前置的数据
     */
    public void proposeItem(M data) {
        dataList.add(0, data);
        if (getHeaderExtraViewCount() == 0) {
            notifyItemInserted(0);
        } else {
            notifyItemInserted(getHeaderExtraViewCount());
        }
    }

    /**
     * 在顶部前置数据集合
     *
     * @param list 要前置的数据集合
     */
    public void proposeList(List<M> list) {
        dataList.addAll(0, list);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //获取不同的类型 已对添加头部和底部视图进行了处理
    @Override
    public final int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (footerView != null && position == dataList.size() + getHeaderExtraViewCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return getCustomViewType(position);
        }
    }

    /**
     * 获取自定义View的类型
     * 需要重写此方法来设置用户自定义的 View类型
     *
     * @param position 位置
     * @return View的类型
     */
    public int getCustomViewType(int position) {
        return 1;
    }

    //获取要显示的数据数量       由于可能添加头部或底部视图需要  + getExtraViewCount()
    @Override
    public int getItemCount() {
        return dataList.size() + getExtraViewCount();
    }

    /**
     * 根据位置获取一条数据
     *
     * @param position View的位置
     * @return 数据
     */
    public M getItem(int position) {
        if (headerView != null && position == 0
                || position >= dataList.size() + getHeaderExtraViewCount()) {
            return null;
        }
        return headerView == null ? dataList.get(position) : dataList.get(position - 1);
    }

    /**
     * 根据ViewHolder获取数据
     *
     * @param holder ViewHolder
     * @return 数据
     */
    public M getItem(VH holder) {
        return getItem(holder.getAdapterPosition());
    }

    public void updateItem(M data) {
        int index = dataList.indexOf(data);
        if (index < 0) {
            return;
        }
        dataList.set(index, data);
        if (headerView == null) {
            notifyItemChanged(index);
        } else {
            notifyItemChanged(index + 1);
        }
    }

    /**
     * 移除一条数据
     *
     * @param position 位置
     */
    public void removeItem(int position) {
        if (headerView == null) {
            dataList.remove(position);
        } else {
            dataList.remove(position - 1);
        }
        notifyItemRemoved(position);
    }

    /**
     * 移除一条数据
     *
     * @param data 要移除的数据
     */
    public void removeItem(M data) {
        int index = dataList.indexOf(data);
        if (index < 0) {
            return;
        }
        dataList.remove(index);
        if (headerView == null) {
            notifyItemRemoved(index);
        } else {
            notifyItemRemoved(index + 1);
        }
    }
    /**
     * 移除所有数据
     *
     */
    public void cleanAllDatas() {
        dataList.clear();
       notifyDataSetChanged();
    }
}
