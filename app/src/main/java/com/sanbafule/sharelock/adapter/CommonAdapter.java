package com.sanbafule.sharelock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.chatting.Interface.OnEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/20.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected OnEventListener listener;

    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        if (datas == null) {
            datas = new ArrayList<>();
        }
        mDatas = datas;
    }

    public void addData(List<T> datas) {
        if (this.mDatas == null) {
            throw new NullPointerException(" This is List is null ");
        }
        if (datas == null || datas.isEmpty()) return;
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void refreshData(List<T> datas) {
        if (datas == null || datas.isEmpty()) return;
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (this.mDatas == null) {
            throw new NullPointerException(" This is List is null ");
        }
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    public void updateItem(T data) {
        int index = mDatas.indexOf(data);
        if (index < 0) {
            return;
        }
        mDatas.set(index, data);
        notifyItemChanged(index);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        convert(holder, mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClickListener(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.OnLongClickListener(position);
                return false;
            }
        });
    }

    public abstract void convert(ViewHolder holder, T t);


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public void setOnEvrntListenner(OnEventListener listenner) {
        this.listener = listenner;
    }
}